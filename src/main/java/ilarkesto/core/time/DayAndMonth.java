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

import ilarkesto.core.base.Args;
import ilarkesto.core.base.Str.Formatable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DayAndMonth implements Comparable<DayAndMonth>, Serializable, Formatable {

	protected int month;
	protected int day;

	private transient int hashCode;

	public DayAndMonth() {
		this(Tm.getNowAsDate());
	}

	public DayAndMonth(java.util.Date javaDate) {
		Args.assertNotNull(javaDate, "javaDate");
		this.month = Tm.getMonth(javaDate);
		this.day = Tm.getDay(javaDate);
	}

	public DayAndMonth(int month, int day) {
		this.month = month;
		this.day = day;
	}

	public DayAndMonth(String dayAndMonthString) {
		Args.assertNotNull(dayAndMonthString, "dayAndMonthString");
		if (dayAndMonthString.length() != 5)
			throw new RuntimeException("Illegal day-and-month format: " + dayAndMonthString);

		int m = Integer.parseInt(dayAndMonthString.substring(0, 2));
		int d = Integer.parseInt(dayAndMonthString.substring(3, 5));

		this.month = m;
		this.day = d;
	}

	public DayAndMonth(long millis) {
		this(Tm.createDate(millis));
	}

	public DayAndMonth(Date date) {
		Args.assertNotNull(date, "date");
		this.month = date.month;
		this.day = date.day;
	}

	// ---

	public Date toDate(int year) {
		return new Date(year, month, day);
	}

	public TimePeriod getPeriodToToday() {
		return toDate(Tm.getCurrentYear()).getPeriodToToday();
	}

	public final boolean isBetween(DayAndMonth begin, DayAndMonth end, boolean includingBoundaries) {
		if (includingBoundaries) {
			return isSameOrAfter(begin) && isSameOrBefore(end);
		} else {
			return isAfter(begin) && isBefore(end);
		}
	}

	public final boolean isSameMonth(DayAndMonth other) {
		return month == other.month;
	}

	public final boolean isSameOrAfter(DayAndMonth other) {
		return compareTo(other) >= 0;
	}

	public final boolean isAfter(DayAndMonth other) {
		return compareTo(other) > 0;
	}

	public final boolean isSameOrBefore(DayAndMonth other) {
		return compareTo(other) <= 0;
	}

	public final boolean isBefore(DayAndMonth other) {
		return compareTo(other) < 0;
	}

	public final boolean isBeforeOrSame(DayAndMonth other) {
		return compareTo(other) <= 0;
	}

	public final boolean isAfterOrSame(DayAndMonth other) {
		return compareTo(other) >= 0;
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

	@Override
	public final int hashCode() {
		if (hashCode == 0) {
			hashCode = 23;
			hashCode = hashCode * 37 + month;
			hashCode = hashCode * 37 + day;
		}
		return hashCode;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj == null) return false;
		DayAndMonth other = (DayAndMonth) obj;
		return other.day == day && other.month == month;
	}

	public boolean equalsIgnoreDay(DayAndMonth d) {
		if (d == null) return false;
		return d.month == month;
	}

	@Override
	public final int compareTo(DayAndMonth other) {
		if (other == null) return 1;
		if (month > other.month) return 1;
		if (month < other.month) return -1;
		if (day > other.day) return 1;
		if (day < other.day) return -1;
		return 0;
	}

	@Override
	public String format() {
		return Tm.getLocalizer().monthDay(month, day);
	}

	public String formatDayShortMonth() {
		StringBuilder sb = new StringBuilder();
		formatDay(sb);
		sb.append(". ");
		sb.append(formatShortMonth());
		return sb.toString();
	}

	public String formatDayLongMonth() {
		StringBuilder sb = new StringBuilder();
		formatDay(sb);
		sb.append(". ");
		sb.append(formatLongMonth());
		return sb.toString();
	}

	public String formatLongMonth() {
		return Month.get(month).toLocalString();
	}

	public String formatShortMonth() {
		return Month.get(month).toLocalShortString();
	}

	public String formatDayMonth() {
		StringBuilder sb = new StringBuilder();
		formatDay(sb);
		sb.append('.');
		formatMonth(sb);
		return sb.toString();
	}

	public String formatMonthDay() {
		StringBuilder sb = new StringBuilder();
		formatMonth(sb);
		sb.append('-');
		formatDay(sb);
		return sb.toString();
	}

	@Override
	public final String toString() {
		return formatMonthDay();
	}

	public void formatDay(StringBuilder sb) {
		if (day < 10) sb.append('0');
		sb.append(day);
	}

	public void formatMonth(StringBuilder sb) {
		if (month < 10) sb.append('0');
		sb.append(month);
	}

	// --- static ---

	public static DayAndMonth today() {
		return new DayAndMonth();
	}

	public static DayAndMonth latest(DayAndMonth... dates) {
		DayAndMonth latest = null;
		for (DayAndMonth date : dates) {
			if (latest == null || date.isAfter(latest)) latest = date;
		}
		return latest;
	}

	public static DayAndMonth earliest(DayAndMonth... dates) {
		DayAndMonth earliest = null;
		for (DayAndMonth date : dates) {
			if (earliest == null || date.isBefore(earliest)) earliest = date;
		}
		return earliest;
	}

	public static Map<Integer, List<DayAndMonth>> groupByMonth(Collection<DayAndMonth> dates) {
		Map<Integer, List<DayAndMonth>> ret = new HashMap<Integer, List<DayAndMonth>>();
		for (DayAndMonth date : dates) {
			Integer month = date.getMonth();
			List<DayAndMonth> list = ret.get(month);
			if (list == null) {
				list = new ArrayList<DayAndMonth>();
				ret.put(month, list);
			}
			list.add(date);
		}
		return ret;
	}

	public static Comparator<DayAndMonth> COMPARATOR = new Comparator<DayAndMonth>() {

		@Override
		public int compare(DayAndMonth a, DayAndMonth b) {
			return a.compareTo(b);
		}

	};

	public static Comparator<DayAndMonth> REVERSE_COMPARATOR = new Comparator<DayAndMonth>() {

		@Override
		public int compare(DayAndMonth a, DayAndMonth b) {
			return b.compareTo(a);
		}

	};

}
