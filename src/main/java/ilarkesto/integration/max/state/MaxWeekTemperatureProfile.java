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

import ilarkesto.core.time.Weekday;

import java.util.ArrayList;
import java.util.List;

public class MaxWeekTemperatureProfile {

	private List<MaxDayTemperatureProfile> dayTemperatureProfiles;

	public static MaxWeekTemperatureProfile createDummy(int variant) {
		MaxWeekTemperatureProfile dummy = new MaxWeekTemperatureProfile();
		dummy.dayTemperatureProfiles = new ArrayList<MaxDayTemperatureProfile>();
		for (int i = 0; i < 7; i++) {
			dummy.dayTemperatureProfiles.add(MaxDayTemperatureProfile.createDummy(i));
		}
		return dummy;
	}

	public MaxDayTemperatureProfile getDayTemperatureProfileForToday() {
		return getDayTemperatureProfile(Weekday.today());
	}

	public MaxDayTemperatureProfile getDayTemperatureProfile(Weekday weekday) {
		switch (weekday) {
			case SATURDAY:
				return dayTemperatureProfiles.get(0);
			case SUNDAY:
				return dayTemperatureProfiles.get(1);
			case MONDAY:
				return dayTemperatureProfiles.get(2);
			case TUESDAY:
				return dayTemperatureProfiles.get(3);
			case WEDNESDAY:
				return dayTemperatureProfiles.get(4);
			case THURSDAY:
				return dayTemperatureProfiles.get(5);
			case FRIDAY:
				return dayTemperatureProfiles.get(6);
		}
		throw new IllegalStateException("Unsupported weekday: " + weekday);
	}

	public List<MaxDayTemperatureProfile> getDayTemperatureProfiles() {
		return dayTemperatureProfiles;
	}

	@Override
	public String toString() {
		return String.valueOf(getDayTemperatureProfiles().size());
	}

}
