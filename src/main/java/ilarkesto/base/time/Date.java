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

import ilarkesto.base.Str;
import ilarkesto.base.Tm;
import ilarkesto.base.Utl;
import ilarkesto.core.time.TimePeriod;
import ilarkesto.core.time.Weekday;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.GregorianCalendar;
import java.util.Locale;

public final class Date extends ilarkesto.core.time.Date {

	public Date() {
		super();
	}

	public Date(java.util.Date javaDate) {
		super(javaDate);
	}

	public Date(long millis) {
		super(millis);
	}

	public Date(int year, int month, int day) {
		super(year, month, day);
	}

	public Date(String date) {
		super(date);
	}

	public Date(GregorianCalendar calendar) {
		this(calendar.getTime());
	}

	@Override
	protected Date newDate(java.util.Date javaDate) {
		return new Date(javaDate);
	}

	@Override
	protected Date newDate(int year, int month, int day) {
		return new Date(year, month, day);
	}

	GregorianCalendar getGregorianCalendar() {
		return new GregorianCalendar(year, month - 1, day);
	}

	public Date getMondayOfWeek() {
		if (getWeekday() == Weekday.MONDAY) return this;
		return addDays(-1).getMondayOfWeek();
	}

	@Override
	public Date getFirstDateOfMonth() {
		return (Date) super.getFirstDateOfMonth();
	}

	public Date getLastDateOfMonth() {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(toJavaDate());
		c.set(GregorianCalendar.DAY_OF_MONTH, c.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
		return new Date(c);
	}

	public Date addMonths(int count) {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(toJavaDate());
		c.add(GregorianCalendar.MONTH, count);
		return new Date(c);
	}

	public Date addYears(int count) {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(toJavaDate());
		c.add(GregorianCalendar.YEAR, count);
		return new Date(c);
	}

	public TimePeriod getPeriodTo(Date other) {
		return new TimePeriod(other.toMillis() - toMillis());
	}

	public TimePeriod getPeriodToNow() {
		return getPeriodTo(today());
	}

	public int getPeriodToInYears(Date other) {
		int years = other.year - year;
		if (month > other.month) {
			years--;
		} else if (month == other.month && day > other.day) {
			years--;
		}
		return years;
	}

	public int getPeriodToInMonths(Date other) {
		int years = other.year - year;
		int months = other.month - month;
		return (years * 12) + months;
	}

	public int getPeriodToNowInMonths() {
		return getPeriodToInMonths(today());
	}

	public String toString(DateFormat format) {
		return format.format(toJavaDate());
	}

	public String toString(Locale locale) {
		if (locale.equals(Locale.GERMANY)) return toString(FORMAT_DAY_MONTH_YEAR);
		return toString();
	}

	@Override
	public Date addDays(int days) {
		return (Date) super.addDays(days);
	}

	@Override
	public Date prevDay() {
		return addDays(-1);
	}

	@Override
	public Date nextDay() {
		return addDays(1);
	}

	// --- static ---

	private static transient Date today;

	private static transient long todayInvalidTime;

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

	public static Date today() {
		if (today == null || Tm.getCurrentTimeMillis() > todayInvalidTime) {
			today = new Date();
			todayInvalidTime = tomorrow().toJavaDate().getTime() - 1;
		}
		return today;
	}

	public static Date tomorrow() {
		return new Date(Tm.getCurrentTimeMillis() + Tm.DAY);
	}

	public static Date inDays(int numberOfDays) {
		return new Date(Tm.getCurrentTimeMillis() + (Tm.DAY * numberOfDays));
	}

	public static Date beforeDays(int numberOfDays) {
		return new Date(Tm.getCurrentTimeMillis() - (Tm.DAY * numberOfDays));
	}

	public static Date randomPast(int beforeMaxDays) {
		return Date.beforeDays(Utl.randomInt(0, beforeMaxDays));
	}

	public static Date parseTolerant(String s) throws ParseException {
		s = s.trim();
		String[] sa = Str.tokenize(s, ".,- ");
		if (sa.length == 0) throw new ParseException("Not a Date: " + s, -1);
		if (sa.length > 3) throw new ParseException("Not a Date: " + s, -1);
		int[] ia = new int[sa.length];
		for (int i = 0; i < ia.length; i++) {
			try {
				ia[i] = Integer.parseInt(sa[i]);
			} catch (NumberFormatException e) {
				throw new ParseException("Not a Date: " + s, -1);
			}
		}

		if (ia.length == 3) return new Date(Tm.year(ia[2]), ia[1], ia[0]);

		Date today = today();
		if (ia.length == 2) {
			if (ia[1] > 12) return new Date(Tm.year(ia[1]), ia[0], today.day);
			return new Date(today.year, ia[1], ia[0]);
		}

		if (ia[0] > 31) return new Date(Tm.year(ia[0]), today.month, today.day);
		return new Date(today.year, today.month, ia[0]);
	}

}
