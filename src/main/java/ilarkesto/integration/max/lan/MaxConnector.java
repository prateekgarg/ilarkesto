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
package ilarkesto.integration.max.lan;

import ilarkesto.core.logging.Log;
import ilarkesto.integration.max.state.DeviceState;
import ilarkesto.integration.max.state.MaxCubeState;
import ilarkesto.integration.max.state.MaxDevice;
import ilarkesto.integration.max.state.MaxRoom;
import ilarkesto.io.Base64;
import ilarkesto.io.IO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * http://www.fhemwiki.de/wiki/MAX
 *
 * http://www.schrankmonster.de/2012/08/17/reverse-engineering-the-elv-max-cube-protocol/
 *
 * http://blog.hekkers.net/2011/08/29/unravelling-the-elv-max-heating-control-system-protocol/
 *
 * https://github.com/Bouni/max-cube-protocol/blob/master/protocol.md
 */
// TODO broadcast to discover cubes
public class MaxConnector {

	private static final Log log = Log.get(MaxConnector.class);

	public static void main(String[] args) throws Exception {
		Log.setDebugEnabled(true);
		new MaxConnector("192.168.0.31").updateState();
	}

	private String host;

	private int port = 62910;
	private MaxCubeState state;

	public MaxConnector(String host) {
		super();
		this.host = host;
	}

	public synchronized void updateState() throws UnknownHostException, IOException {
		Socket socket = new Socket(host, port);
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		state = new MaxCubeState();
		String line;
		while ((line = in.readLine()) != null) {
			parseLine(line);
		}
		IO.close(socket);
	}

	private void parseLine(String line) {
		log.info("line:", line);
		if (line.startsWith("H:")) {
			parseLineH(line.substring(2));
		} else if (line.startsWith("M")) {
			parseLineM(line.substring(2));
		} else if (line.startsWith("C")) {
			parseLineC(line.substring(2));
		} else if (line.startsWith("L")) {
			parseLineL(line.substring(2));
		} else {
			log.warn("Unsupported message:", line);
		}
	}

	private void parseLineL(String line) {}

	private void parseLineC(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line, ",");
		String rfAddress = tokenizer.nextToken();
		String dataAsString = tokenizer.nextToken();
		byte[] data = Base64.decode(dataAsString);

		DeviceState state = new DeviceState() {};

		int offset = 0;
		int dataLen = data[offset];
		offset++;

		updateProperty(state, "radioAddress", extractHex(data, offset, 3));
		offset += 3;

		updateProperty(state, "type", data[offset]);
		offset++;

		updateProperty(state, "?", extractHex(data, offset, 3));
		offset += 3;

		updateProperty(state, "serial", extractString(data, offset, 10));
		offset += 10;

		updateProperty(state, "rest", extractString(data, offset, data.length - offset));
	}

	private void parseLineM(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line, ",");
		String index = tokenizer.nextToken();
		String count = tokenizer.nextToken();
		String dataAsString = tokenizer.nextToken();
		byte[] data = Base64.decode(dataAsString);

		int offset = 2;
		int roomCount = data[offset];
		offset++;

		List<MaxRoom> rooms = new ArrayList<MaxRoom>(roomCount);
		for (int i = 0; i < roomCount; i++) {
			MaxRoom room = new MaxRoom();
			updateProperty(room, "id", data[offset + 0]);
			offset++;

			int nameLength = data[offset];
			offset++;

			updateProperty(room, "name", extractString(data, offset, nameLength));
			offset += nameLength;

			offset += 3;

			rooms.add(room);
		}
		updateProperty(state, "rooms", rooms);

		int deviceCount = data[offset];
		offset++;

		List<MaxDevice> devices = new ArrayList<MaxDevice>(deviceCount);
		for (int i = 0; i < deviceCount; i++) {
			MaxDevice device = new MaxDevice();
			updateProperty(device, "deviceType", data[offset]);
			offset++;

			updateProperty(device, "radioAddress", extractHex(data, offset, 3));
			offset += 3;

			updateProperty(device, "serialNumber", extractString(data, offset, 10));
			offset += 10;

			int nameLength = data[offset];
			offset++;

			updateProperty(device, "name", extractString(data, offset, nameLength));
			offset += nameLength;

			updateProperty(device, "room", data[offset]);
			offset++;

			devices.add(device);
		}
		updateProperty(state, "devices", devices);

	}

	private String extractString(byte[] data, int start, int len) {
		byte[] bytes = new byte[len];
		System.arraycopy(data, start, bytes, 0, len);
		return new String(bytes, Charset.forName(IO.UTF_8));
	}

	private String extractHex(byte[] data, int start, int len) {
		StringBuilder sb = new StringBuilder(len * 2);
		for (int i = 0; i < len; i++) {
			sb.append(String.format("%02x", data[i] & 0xff));
		}
		return sb.toString();
	}

	/**
	 * HEADER
	 */
	private void parseLineH(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line, ",");
		if (tokenizer.hasMoreTokens()) updateProperty(state, "serialNumber", tokenizer.nextToken());
		if (tokenizer.hasMoreTokens()) updateProperty(state, "rfAddress", tokenizer.nextToken());
		if (tokenizer.hasMoreTokens()) updateProperty(state, "firmwareVersion", tokenizer.nextToken());
		if (tokenizer.hasMoreTokens()) updateProperty(state, "?", tokenizer.nextToken());
		if (tokenizer.hasMoreTokens()) updateProperty(state, "?httpConnectionId", tokenizer.nextToken());
		if (tokenizer.hasMoreTokens()) updateProperty(state, "?dutyCycle", tokenizer.nextToken());
		if (tokenizer.hasMoreTokens()) updateProperty(state, "?freeMemorySlots", tokenizer.nextToken());
		if (tokenizer.hasMoreTokens()) updateProperty(state, "cubeDate", tokenizer.nextToken());
		if (tokenizer.hasMoreTokens()) updateProperty(state, "?cubeTime", tokenizer.nextToken());
		if (tokenizer.hasMoreTokens()) updateProperty(state, "stateCubeTime", tokenizer.nextToken());
		if (tokenizer.hasMoreTokens()) updateProperty(state, "NTPCounter", tokenizer.nextToken());
	}

	private void updateProperty(Object object, String property, Object value) {
		log.info(object.getClass().getSimpleName() + "." + property, "->", value);
	}

}
