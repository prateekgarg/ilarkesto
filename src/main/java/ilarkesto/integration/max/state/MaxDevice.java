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

public class MaxDevice {

	private String deviceType;
	private int radioAddress;
	private String serialNumber;
	private String name;

	private String radioState;
	private DeviceState state;
	private String stateInfo;

	private MaxRoom room;

	public static MaxDevice createDummyForRoom(int variant) {
		MaxDevice dummy = new MaxDevice();
		dummy.deviceType = "? roomDevice";
		dummy.name = "dummy room device " + variant;
		dummy.radioAddress = 0;
		dummy.radioState = "radioState";
		dummy.serialNumber = "serial123";
		dummy.state = variant == 0 ? MaxShutterContactDeviceState.createDummy(variant)
				: MaxHeatingThermostatDeviceState.createDummy(variant);
		dummy.stateInfo = "state info";
		return dummy;
	}

	public static MaxDevice createDummyForHouse() {
		MaxDevice dummy = new MaxDevice();
		dummy.deviceType = "? houseDevice";
		dummy.name = "dummy house device";
		dummy.radioAddress = 0;
		dummy.radioState = "radioState";
		dummy.serialNumber = "serial123";
		dummy.state = MaxPushButtonDeviceState.createDummy();
		dummy.stateInfo = "state info";
		return dummy;
	}

	public boolean isRadioOk() {
		return isRadioStateOk() && !getState().isTransmitError();
	}

	public boolean isRadioStateOk() {
		return "Ok".equals(getRadioState());
	}

	public boolean isStateInfoValid() {
		return "Valid".equals(getStateInfo());
	}

	public String getNameWithRoomName() {
		MaxRoom r = getRoom();
		if (r == null) return getName();
		return r.getName() + ", " + getName();
	}

	public String getDeviceType() {
		return deviceType;
	}

	public boolean isDeviceTypePushButton() {
		return "PushButton".equals(getDeviceType());
	}

	public boolean isDeviceTypeShutterContact() {
		return "ShutterContact".equals(getDeviceType());
	}

	public boolean isDeviceTypeWallMountedThermostat() {
		return "WallMountedThermostat".equals(getDeviceType());
	}

	public String getName() {
		return name;
	}

	public int getRadioAddress() {
		return radioAddress;
	}

	public String getRadioState() {
		return radioState;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public DeviceState getState() {
		return state;
	}

	public String getStateInfo() {
		return stateInfo;
	}

	@Override
	public String toString() {
		return deviceType + ": " + name + " " + state;
	}

	public void wire(MaxRoom room) {
		this.room = room;
	}

	public MaxRoom getRoom() {
		return room;
	}

}
