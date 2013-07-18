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

import ilarkesto.base.Utl;
import ilarkesto.testng.ATest;

import java.net.DatagramPacket;
import java.util.LinkedList;
import java.util.List;

import org.testng.annotations.Test;

public class UdpNodeTest extends ATest {

	@Test
	public void twoNodes() {
		DummyObserver observer1 = new DummyObserver();
		UdpNode node1 = new UdpNode(observer1);
		DummyObserver observer2 = new DummyObserver();
		UdpNode node2 = new UdpNode(observer2);

		node1.start(10001);
		node2.start(10002);

		Utl.sleep(1000);

		node1.sendPacket(new UdpAddress("localhost", 10002), "hello".getBytes());

		Utl.sleep(1000);

		node1.requestStop();
		node2.requestStop();

		assertSize(observer2.packets, 1);
	}

	@Test(enabled = false)
	public void singleNode() {
		DummyObserver observer = new DummyObserver();
		UdpNode node = new UdpNode(observer);
		node.start(10001);
		Utl.sleep(1000);
		node.requestStop();
	}

	class DummyObserver implements UdpNodeObserver {

		private List<DatagramPacket> packets = new LinkedList<DatagramPacket>();

		@Override
		public synchronized void onPacketReceived(DatagramPacket packet) {
			packets.add(packet);
		}

	}

}
