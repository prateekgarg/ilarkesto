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

import ilarkesto.integration.max.Max;

import java.util.Date;

public class MaxCubeLastPing {

	private Date date;
	private boolean outdated;

	public static MaxCubeLastPing createDummy() {
		MaxCubeLastPing dummy = new MaxCubeLastPing();
		dummy.date = new Date(System.currentTimeMillis() - 10000);
		dummy.outdated = false;
		return dummy;
	}

	public Date getDate() {
		return date;
	}

	public boolean isOutdated() {
		return outdated;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(Max.formatDateTime(date));
		if (outdated) sb.append(" outdated");
		return sb.toString();
	}

}
