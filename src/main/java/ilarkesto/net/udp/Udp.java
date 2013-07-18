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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Udp {

	public static int PACKET_LENGHT_GUARANTEED = 576;
	public static int PACKET_LENGHT_MAX_GUESS = 1400;

	// public static void sendPacket(DatagramSocket socket, SocketAddress receiver, byte[] data) throws
	// IOException {
	// if (socket == null) throw new IllegalArgumentException("socket == null");
	// DatagramPacket dp = createPacket(receiver, data);
	// synchronized (socket) {
	// socket.send(dp);
	// }
	// }

	public static DatagramPacket receivePacket(DatagramSocket socket, int length) throws IOException {
		if (socket == null) throw new IllegalArgumentException("socket == null");
		DatagramPacket dp = createPacket(length);
		synchronized (socket) {
			socket.receive(dp);
		}
		return dp;
	}

	public static DatagramPacket createPacket(int length) {
		return new DatagramPacket(new byte[length], length);
	}

	public static DatagramPacket createPacket(String receiverHost, int receiverPort, byte[] data) {
		InetAddress address;
		try {
			address = InetAddress.getByName(receiverHost);
		} catch (UnknownHostException ex) {
			throw new RuntimeException(ex);
		}
		return new DatagramPacket(data, data.length, address, receiverPort);
	}

}
