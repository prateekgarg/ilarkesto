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
package ilarkesto.integration.datev;

import ilarkesto.core.time.Date;

public class DATEV {

	public static String formatDateTTMM(Date date) {
		if (date == null) return null;
		int day = date.getDay();
		int month = date.getMonth();

		StringBuilder sb = new StringBuilder();

		if (day < 10) sb.append("0");
		sb.append(day);

		if (month < 10) sb.append("0");
		sb.append(month);

		return sb.toString();
	}

}
