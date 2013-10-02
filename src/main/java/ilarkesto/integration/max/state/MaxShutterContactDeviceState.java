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
package ilarkesto.integration.max.state;

public class MaxShutterContactDeviceState extends DeviceState {

	private boolean windowOpen;

	public static MaxShutterContactDeviceState createDummy(int variant) {
		MaxShutterContactDeviceState dummy = new MaxShutterContactDeviceState();
		dummy.windowOpen = variant == 1;
		dummy.batteryLow = false;
		dummy.transmitError = false;
		return dummy;
	}

	public boolean isWindowOpen() {
		return windowOpen;
	}

	@Override
	public String toString() {
		if (windowOpen) return "windowOpen " + super.toString();
		return super.toString();
	}

}
