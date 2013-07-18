/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package ilarkesto.net.udp;

import ilarkesto.core.base.ALoopRunnable;
import ilarkesto.core.logging.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class UdpNode {

	private static Log log = Log.get(UdpNode.class);

	private int maxPacketSize = Udp.PACKET_LENGHT_GUARANTEED;

	private UdpNodeObserver observer;

	private long errorSleepTime = 2342;
	private boolean stopRequested = true;
	private int port;
	private DatagramSocket socket;
	private BlockingQueue<DatagramPacket> receivedPackets = new LinkedBlockingQueue<DatagramPacket>();
	private BlockingQueue<DatagramPacket> outgoingPackets = new LinkedBlockingQueue<DatagramPacket>();
	private Executor executor = Executors.newCachedThreadPool();

	public UdpNode(UdpNodeObserver observer) {
		super();
		this.observer = observer;
	}

	public synchronized void start(int port) {
		if (!stopRequested) throw new IllegalStateException("UdpNode already running on port" + this.port);
		this.port = port;
		stopRequested = false;
		executor.execute(new DatagramSender());
		executor.execute(new DatagramReceiver());
		executor.execute(new DatagramProcessor());
	}

	public synchronized void requestStop() {
		if (stopRequested) return;
		stopRequested = true;
		log.debug("Stop requested");
	}

	public synchronized void sendPacket(UdpAddress receiver, byte[] data) {
		if (stopRequested) throw new IllegalStateException("Udp node not started");
		DatagramPacket packet = Udp.createPacket(receiver.getHost(), receiver.getPort(), data);
		outgoingPackets.add(packet);
	}

	public SocketAddress getSocketAddress() {
		if (socket == null) return null;
		return socket.getLocalSocketAddress();
	}

	public int getPort() {
		return port;
	}

	private void errorSleep() throws InterruptedException {
		Thread.sleep(errorSleepTime);
	}

	private DatagramSocket getSocket() throws SocketException {
		if (socket == null) createSocket();
		return socket;
	}

	private void createSocket() throws SocketException {
		closeSocket();
		socket = new DatagramSocket(port);
		log.info("Socket created:", socket.getLocalSocketAddress());
	}

	private void closeSocket() {
		if (socket != null) {
			if (!socket.isClosed()) socket.close();
			socket = null;
		}
	}

	class DatagramReceiver extends ALoopRunnable {

		@Override
		protected void onIteration() throws InterruptedException {
			try {
				receivePacket();
			} catch (SocketException ex) {
				throw new RuntimeException(ex);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
			if (stopRequested) abort();
		}

		private void receivePacket() throws InterruptedException, SocketException, IOException {
			DatagramPacket packet = Udp.receivePacket(getSocket(), maxPacketSize);
			receivedPackets.put(packet);
			log.debug("Packet received", packet);
		}

		@Override
		protected void onIterationException(Exception ex) throws InterruptedException {
			log.info(ex);
			errorSleep();
		}
	}

	class DatagramSender extends ALoopRunnable {

		@Override
		protected void onIteration() throws InterruptedException {
			sendNextPacket();
			if (stopRequested) abort();
		}

		private void sendNextPacket() throws InterruptedException {
			DatagramPacket packet = outgoingPackets.take();
			try {
				getSocket().send(packet);
			} catch (IOException ex) {
				outgoingPackets.add(packet); // insert would be better
				throw new RuntimeException(ex);
			}
			log.debug("Packet sent", packet);
		}

		@Override
		protected void onIterationException(Exception ex) throws InterruptedException {
			log.info(ex);
			errorSleep();
		}
	}

	class DatagramProcessor extends ALoopRunnable {

		@Override
		protected void onIteration() throws InterruptedException {
			DatagramPacket packet = receivedPackets.take();
			if (stopRequested) {
				abort();
				return;
			}
			try {
				observer.onPacketReceived(packet);
			} catch (Exception ex) {
				log.error("Processing received packet failed:", ex);
			}
			if (stopRequested) abort();
		}

		@Override
		protected void onIterationException(Exception ex) throws InterruptedException {
			log.info(ex);
			errorSleep();
		}
	}

}
