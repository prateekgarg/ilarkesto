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

public class MaxHeatingThermostatDeviceState extends DeviceState {

	private float setPointTemperature;
	private float temperatureOffset;

	public static MaxHeatingThermostatDeviceState createDummy(int variant) {
		MaxHeatingThermostatDeviceState dummy = new MaxHeatingThermostatDeviceState();
		dummy.setPointTemperature = 42.23f;
		dummy.temperatureOffset = variant == 0 ? 1 : 0;
		dummy.batteryLow = false;
		dummy.transmitError = false;
		return dummy;
	}

	public float getSetPointTemperature() {
		return setPointTemperature;
	}

	public float getTemperatureOffset() {
		return temperatureOffset;
	}

	@Override
	public String toString() {
		return setPointTemperature + " " + temperatureOffset + " " + super.toString();
	}

}
