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
package ilarkesto.integration.max;

import ilarkesto.integration.max.state.MaxDevice;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class Max {

	public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

	public static String formatDateTime(Date date) {
		if (date == null) return null;
		return new SimpleDateFormat(DATE_TIME_PATTERN).format(date);
	}

	public static String getNamesWithRoomNames(Collection<MaxDevice> devices) {
		if (devices == null || devices.isEmpty()) return null;
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (MaxDevice device : devices) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append(device.getNameWithRoomName());
		}
		return sb.toString();
	}
}
