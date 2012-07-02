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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Date implements Comparable<Date>, Serializable {

	protected int year;
	protected int month;
	protected int day;

	private transient int hashCode;

	public Date() {
		this(Tm.getNowAsDate());
	}

	public Date(java.util.Date javaDate) {
		this.year = Tm.getYear(javaDate);
		this.month = Tm.getMonth(javaDate);
		this.day = Tm.getDay(javaDate);
	}

	public Date(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}

	public Date(String date) {
		if (date.length() != 10) throw new RuntimeException("Illegal date format: " + date);

		int y = Integer.parseInt(date.substring(0, 4));
		int m = Integer.parseInt(date.substring(5, 7));
		int d = Integer.parseInt(date.substring(8, 10));

		this.year = y;
		this.month = m;
		this.day = d;
	}

	public Date(long millis) {
		this(Tm.createDate(millis));
	}

	protected Date newDate(java.util.Date javaDate) {
		return new Date(javaDate);
	}

	// ---

	public final int getWeek() {
		return Tm.getWeek(toJavaDate());
	}

	public TimePeriod getPeriodTo(Date other) {
		return new TimePeriod(Tm.DAY * Tm.getDaysBetweenDates(toJavaDate(), other.toJavaDate()));
	}

	public TimePeriod getPeriodToToday() {
		return getPeriodTo(today());
	}

	public Weekday getWeekday() {
		return Weekday.get(Tm.getWeekday(toJavaDate()));
	}

	public Date addDays(int days) {
		return newDate(Tm.addDays(toJavaDate(), days));
	}

	public Date prevDay() {
		return addDays(-1);
	}

	public Date nextDay() {
		return addDays(1);
	}

	public final boolean isBetween(Date begin, Date end, boolean includingBoundaries) {
		if (includingBoundaries) {
			return isSameOrAfter(begin) && isSameOrBefore(end);
		} else {
			return isAfter(begin) && isBefore(end);
		}
	}

	public final boolean isSameMonthAndYear(Date other) {
		return month == other.month && year == other.year;
	}

	public final boolean isSameOrAfter(Date other) {
		return compareTo(other) >= 0;
	}

	public final boolean isAfter(Date other) {
		return compareTo(other) > 0;
	}

	public final boolean isSameOrBefore(Date other) {
		return compareTo(other) <= 0;
	}

	public final boolean isBefore(Date other) {
		return compareTo(other) < 0;
	}

	public final boolean isBeforeOrSame(Date other) {
		return compareTo(other) <= 0;
	}

	public final boolean isPast() {
		return isBefore(today());
	}

	public final boolean isAfterOrSame(Date other) {
		return compareTo(other) >= 0;
	}

	public final boolean isTomorrow() {
		return equals(today().addDays(1));
	}

	public final boolean isYesterday() {
		return equals(today().addDays(-1));
	}

	public final boolean isFuture() {
		return isAfter(today());
	}

	public final boolean isFutureOrToday() {
		return isAfterOrSame(today());
	}

	public final boolean isPastOrToday() {
		return isBeforeOrSame(today());
	}

	public final java.util.Date toJavaDate() {
		return Tm.createDate(year, month, day);
	}

	public final java.util.Date toJavaDate(Time time) {
		return Tm.createDate(year, month, day, time.hour, time.minute, time.second);
	}

	public final long toMillis(Time time) {
		return toJavaDate(time).getTime();
	}

	public final long toMillis() {
		return toJavaDate().getTime();
	}

	public final boolean isToday() {
		return equals(today());
	}

	public final int getDay() {
		return day;
	}

	public final int getMonth() {
		return month;
	}

	public final int getYear() {
		return year;
	}

	@Override
	public final int hashCode() {
		if (hashCode == 0) {
			hashCode = 23;
			hashCode = hashCode * 37 + year;
			hashCode = hashCode * 37 + month;
			hashCode = hashCode * 37 + day;
		}
		return hashCode;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj == null) return false;
		Date other = (Date) obj;
		return other.day == day && other.month == month && other.year == year;
	}

	@Override
	public final int compareTo(Date other) {
		if (other == null) return 1;
		if (year > other.year) return 1;
		if (year < other.year) return -1;
		if (month > other.month) return 1;
		if (month < other.month) return -1;
		if (day > other.day) return 1;
		if (day < other.day) return -1;
		return 0;
	}

	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(year);
		sb.append("-");
		if (month < 10) sb.append('0');
		sb.append(month);
		sb.append("-");
		if (day < 10) sb.append('0');
		sb.append(day);
		return sb.toString();
	}

	// --- static ---

	public static Date today() {
		return new Date();
	}

	public static Date latest(Date... dates) {
		Date latest = null;
		for (Date date : dates) {
			if (latest == null || date.isAfter(latest)) latest = date;
		}
		return latest;
	}

	public static Date earliest(Date... dates) {
		Date earliest = null;
		for (Date date : dates) {
			if (earliest == null || date.isBefore(earliest)) earliest = date;
		}
		return earliest;
	}

	public static Date tomorrow() {
		return today().nextDay();
	}

	public static Date inDays(int numberOfDays) {
		return today().addDays(numberOfDays);
	}

	public static Date beforeDays(int numberOfDays) {
		return today().addDays(-numberOfDays);
	}

	public static List<Date> getDaysInMonth(int year, int month) {
		List<Date> dates = new ArrayList<Date>();
		Date d = new Date(year, month, 1);

		while (d.getMonth() == month) {
			dates.add(d);
			d = d.nextDay();
		}

		return dates;
	}

	public static Comparator<Date> COMPARATOR = new Comparator<Date>() {

		@Override
		public int compare(Date a, Date b) {
			return a.compareTo(b);
		}

	};

	public static Comparator<Date> REVERSE_COMPARATOR = new Comparator<Date>() {

		@Override
		public int compare(Date a, Date b) {
			return b.compareTo(a);
		}

	};

}
