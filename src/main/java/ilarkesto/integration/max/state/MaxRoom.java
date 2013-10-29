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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MaxRoom {

	private int id;
	private String name;
	private int order;
	private int boostDuration;
	private int boostValveAngle;
	private float comfortTemperature;
	private float ecoTemperature;
	private float maximumTemperature;
	private float setPointTemperature;
	private boolean setPointTemperatureValid;
	private String controlMode;
	private String decalcificationDay;
	private int decalcificationHour;
	private List<MaxDevice> devices;
	private boolean stateChanged;
	private boolean stateDirty;
	private boolean temperatureControllable;
	private String temperatureMode;
	private Date temporaryModeStopDate;
	private MaxWeekTemperatureProfile weekTemperatureProfile;
	private int windowOpenDuration;
	private float windowOpenTemperature;
	private int maximumNoOfHeatingThermostats;
	private int maximumNoOfShutterContacts;
	private int maximumNoOfWallMountedThermostats;

	public static MaxRoom createDummy(int variant) {
		MaxRoom dummy = new MaxRoom();
		dummy.id = variant;
		dummy.name = "room " + variant;
		dummy.order = variant;
		dummy.boostDuration = 23;
		dummy.boostValveAngle = 23;
		dummy.comfortTemperature = 42.23f;
		dummy.ecoTemperature = 23.42f;
		dummy.maximumTemperature = 99;
		dummy.setPointTemperature = 33.33f;
		dummy.setPointTemperatureValid = variant != 1;
		dummy.controlMode = "controlMode";
		dummy.decalcificationDay = "decalcificationDay";
		dummy.devices = new ArrayList<MaxDevice>();
		dummy.devices.add(MaxDevice.createDummyForRoom(0));
		dummy.devices.add(MaxDevice.createDummyForRoom(1));
		dummy.stateChanged = variant == 2;
		dummy.stateDirty = variant == 3;
		dummy.temperatureControllable = variant == 4;
		dummy.temperatureMode = "temperatureMode";
		dummy.temporaryModeStopDate = new Date(System.currentTimeMillis() + 12345678);
		dummy.weekTemperatureProfile = MaxWeekTemperatureProfile.createDummy(variant);
		dummy.windowOpenDuration = variant;
		dummy.windowOpenTemperature = 11.11f;
		dummy.maximumNoOfHeatingThermostats = 23;
		dummy.maximumNoOfShutterContacts = 23;
		dummy.maximumNoOfWallMountedThermostats = 23;
		return dummy;
	}

	public boolean isWindowOpen() {
		for (MaxDevice device : getDevices()) {
			if (!device.isDeviceTypeShutterContact()) continue;
			MaxShutterContactDeviceState state = (MaxShutterContactDeviceState) device.getState();
			if (state.isWindowOpen()) return true;
		}
		return false;
	}

	/**
	 * permanent or temporary warming
	 */
	public boolean isManualWarming() {
		if (isControlModeAuto()) return false;
		return getSetPointTemperature() > getEcoTemperature();
	}

	public boolean isAutoOrEco() {
		return isControlModeAuto() || isTemperatureModeEco();
	}

	@Override
	public String toString() {
		return name;
	}

	public int getMaximumNoOfShutterContacts() {
		return maximumNoOfShutterContacts;
	}

	public int getMaximumNoOfWallMountedThermostats() {
		return maximumNoOfWallMountedThermostats;
	}

	public int getMaximumNoOfHeatingThermostats() {
		return maximumNoOfHeatingThermostats;
	}

	public float getWindowOpenTemperature() {
		return windowOpenTemperature;
	}

	public int getWindowOpenDuration() {
		return windowOpenDuration;
	}

	public MaxWeekTemperatureProfile getWeekTemperatureProfile() {
		return weekTemperatureProfile;
	}

	public Date getTemporaryModeStopDate() {
		return temporaryModeStopDate;
	}

	public String getTemperatureMode() {
		return temperatureMode;
	}

	public boolean isTemperatureModeNormal() {
		return "Normal".equals(getTemperatureMode());
	}

	public boolean isTemperatureModeEco() {
		return "Eco".equals(getTemperatureMode());
	}

	public boolean isTemperatureModeComfort() {
		return "Comfort".equals(getTemperatureMode());
	}

	public boolean isTemperatureControllable() {
		return temperatureControllable;
	}

	public boolean isStateDirty() {
		return stateDirty;
	}

	public boolean isStateChanged() {
		return stateChanged;
	}

	public List<MaxDevice> getDevices() {
		return devices;
	}

	public List<MaxDevice> getDevicesWithLowBattery() {
		List<MaxDevice> ret = new ArrayList<MaxDevice>();
		for (MaxDevice device : getDevices()) {
			if (device.getState().isBatteryLow()) ret.add(device);
		}
		return ret;
	}

	public List<MaxDevice> getDevicesWithTransmitError() {
		List<MaxDevice> ret = new ArrayList<MaxDevice>();
		for (MaxDevice device : getDevices()) {
			if (device.getState().isTransmitError()) ret.add(device);
		}
		return ret;
	}

	public int getDecalcificationHour() {
		return decalcificationHour;
	}

	public String getDecalcificationDay() {
		return decalcificationDay;
	}

	public String getControlMode() {
		return controlMode;
	}

	public boolean isControlModeAuto() {
		return "Auto".equals(getControlMode());
	}

	public boolean isControlModePermanently() {
		return "Permanently".equals(getControlMode());
	}

	public boolean isControlModeTemporary() {
		return "Temporary".equals(getControlMode());
	}

	public boolean isSetPointTemperatureValid() {
		return setPointTemperatureValid;
	}

	public float getSetPointTemperature() {
		return setPointTemperature;
	}

	public float getMaximumTemperature() {
		return maximumTemperature;
	}

	public float getEcoTemperature() {
		return ecoTemperature;
	}

	public float getComfortTemperature() {
		return comfortTemperature;
	}

	public int getBoostValveAngle() {
		return boostValveAngle;
	}

	public int getOrder() {
		return order;
	}

	public int getBoostDuration() {
		return boostDuration;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	void wire() {
		for (MaxDevice device : getDevices()) {
			device.wire(this);
		}
	}

}
