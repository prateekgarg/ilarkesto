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

import ilarkesto.core.time.Time;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MaxDayTemperatureProfile {

	private String dayOfWeek;
	private List<MaxTemperatureProfilSwitchPoint> switchPoints;

	public static MaxDayTemperatureProfile createDummy(int dayOfWeek) {
		MaxDayTemperatureProfile dummy = new MaxDayTemperatureProfile();
		dummy.dayOfWeek = String.valueOf(dayOfWeek);
		dummy.switchPoints = new ArrayList<MaxTemperatureProfilSwitchPoint>();
		dummy.switchPoints.add(MaxTemperatureProfilSwitchPoint.createDummy(0));
		dummy.switchPoints.add(MaxTemperatureProfilSwitchPoint.createDummy(1));
		return dummy;
	}

	public MaxTemperatureProfilSwitchPoint getSwitchPointForNow() {
		return getSwitchPoint(Time.now());
	}

	public MaxTemperatureProfilSwitchPoint getSwitchPoint(Time time) {
		List<MaxTemperatureProfilSwitchPoint> points = new ArrayList<MaxTemperatureProfilSwitchPoint>(getSwitchPoints());
		if (points.isEmpty()) return null;

		Collections.reverse(points);

		MaxTemperatureProfilSwitchPoint best = null;
		for (MaxTemperatureProfilSwitchPoint sp : points) {
			if (best == null) {
				best = sp;
				continue;
			}
			Time stop = sp.getStopAsTime();
			if (stop.isBefore(time)) return best;
			best = sp;
		}
		return best;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public List<MaxTemperatureProfilSwitchPoint> getSwitchPoints() {
		return switchPoints;
	}

	@Override
	public String toString() {
		return dayOfWeek;
	}

}
