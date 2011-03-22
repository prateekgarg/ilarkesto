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
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.core.time;

import java.io.Serializable;

public class DateAndTime implements Comparable<DateAndTime>, Serializable {

	protected Date date;
	protected Time time;

	private transient int hashCode;

	public DateAndTime(java.util.Date javaDate) {
		this(new Date(javaDate), new Time(javaDate));
	}

	public DateAndTime(String s) {
		assert s != null;
		s = s.trim();
		int idx = s.indexOf(' ');

		if (idx > 0) {
			String sDate = s.substring(0, idx);
			String sTime = s.substring(idx + 1);
			date = new Date(sDate);
			time = new Time(sTime);
		} else {
			if (s.indexOf('.') > 0) {
				date = new Date(s);
				time = new Time("0");
			} else {
				date = Date.today();
				time = new Time(s);
			}
		}
	}

	public DateAndTime(Date date, Time time) {
		assert date != null && time != null;
		this.date = date;
		this.time = time;
	}

	public DateAndTime(int year, int month, int day, int hour, int minute, int second) {
		this(new Date(year, month, day), new Time(hour, minute, second));
	}

	public DateAndTime(long millis) {
		this(Tm.createDate(millis));
	}

	public DateAndTime() {
		this(Tm.getNowAsDate());
	}

	// ---

	public TimePeriod getPeriodTo(DateAndTime other) {
		return new TimePeriod(other.toMillis() - toMillis());
	}

	public TimePeriod getPeriodToNow() {
		return getPeriodTo(now());
	}

	public TimePeriod getPeriodFromNow() {
		return now().getPeriodTo(this);
	}

	public final boolean isBefore(DateAndTime other) {
		return compareTo(other) < 0;
	}

	public final boolean isAfter(DateAndTime other) {
		return compareTo(other) > 0;
	}

	public final Date getDate() {
		return date;
	}

	public final Time getTime() {
		return time;
	}

	public final java.util.Date toJavaDate() {
		return Tm.createDate(date.toMillis() + time.toMillis());
	}

	public final long toMillis() {
		return date.toMillis() + time.toMillis();
	}

	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(date.toString());
		sb.append(" ");
		sb.append(time.toString());
		return sb.toString();
	}

	@Override
	public final int hashCode() {
		if (hashCode == 0) {
			hashCode = 23;
			hashCode = hashCode * 37 + date.hashCode();
			hashCode = hashCode * 37 + time.hashCode();
		}
		return hashCode;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof DateAndTime)) return false;
		return date.equals(((DateAndTime) obj).date) && time.equals(((DateAndTime) obj).time);
	}

	@Override
	public final int compareTo(DateAndTime o) {
		int i = date.compareTo(o.date);
		return i == 0 ? time.compareTo(o.time) : i;
	}

	// --- static ---

	public static DateAndTime now() {
		return new DateAndTime();
	}

}
