/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>, Artjom Kochtchi
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.base.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Time extends ilarkesto.core.time.Time {

	public static final transient SimpleDateFormat FORMAT_HOUR_MINUTE_SECOND = new SimpleDateFormat("HH:mm:ss");

	public static final transient SimpleDateFormat FORMAT_HOUR_MINUTE_SECOND_NOSEP = new SimpleDateFormat("HHmmss");

	public Time() {
		super();
	}

	public Time(int hour, int minute) {
		super(hour, minute, 0);
	}

	public Time(int hour, int minute, int second) {
		super(hour, minute, second);
	}

	public Time(String timeString) {
		super(timeString);
	}

	public Time(GregorianCalendar calendar) {
		this(calendar.get(GregorianCalendar.HOUR_OF_DAY), calendar.get(GregorianCalendar.MINUTE), calendar
				.get(GregorianCalendar.SECOND));
	}

	// ---

	public TimePeriod getPeriodTo(Time other) {
		return new TimePeriod(other.toMillis() - toMillis());
	}

	// @Deprecated
	// public final String toString(DateFormat format) {
	// return toString(format, Date.today());
	// }

	public final String toString(DateFormat format, Date date) {
		return format.format(getJavaDateOn(date));
	}

	public final String toString(Locale locale) {
		StringBuilder sb = new StringBuilder();
		if (hour < 10) sb.append("0");
		sb.append(hour);
		sb.append(":");
		if (minute < 10) sb.append("0");
		sb.append(minute);
		return sb.toString();
	}

	// --- static ---

	public static String toStringBeginEnd(Time begin, Time end) {
		if (begin == null && end == null) return null;
		StringBuilder sb = new StringBuilder();
		if (begin != null) {
			sb.append(begin);
		}
		if (end != null) {
			sb.append("-");
			sb.append(end);
		}
		return sb.toString();
	}

	public static Time now() {
		return new Time();
	}

}
