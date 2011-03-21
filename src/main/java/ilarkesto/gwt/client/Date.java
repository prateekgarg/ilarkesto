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
package ilarkesto.gwt.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Date extends ilarkesto.core.time.Date implements IsSerializable {

	private int year;
	private int month;
	private int day;

	public Date() {
		super();
	}

	public Date(int year, int month, int day) {
		super(year, month, day);
	}

	public Date(String date) {
		super(date);
	}

	public Date(java.util.Date javaDate) {
		super(javaDate);
	}

	@Override
	protected Date newDate(java.util.Date javaDate) {
		return new Date(javaDate);
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

	public static List<Date> getDaysOverMonth(int year, int month) {
		List<Date> dates = new ArrayList<Date>();
		Date d = new Date(year, month, 1);

		int days = getDaysInMonth(year, month).size();
		if (d.getWeekday() != 1) {
			// from monday till first day of month
			while (d.getWeekday() != 1) {
				d = d.prevDay();
				days++;
			}
		}

		for (int i = 0; i < days; i++) {
			dates.add(d);
			d = d.nextDay();
		}

		if (d.getWeekday() != 0) {
			// from last day of month till sunday
			while (d.getWeekday() != 0) {
				dates.add(d);
				d = d.nextDay();
			}
			dates.add(d);
		}

		return dates;

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

	public String getWeekdayLabel() {
		return Gwt.formatWeekdayShort(toJavaDate());
	}

	public int getWeekday() {
		return toJavaDate().getDay();
	}

	public int getWeek() {
		java.util.Date jFirstJan = new Date(year, 1, 1).toJavaDate();
		int firstMonday = jFirstJan.getDay() < 1 ? 2 : (jFirstJan.getDay() > 1 ? 9 - jFirstJan.getDay() : 1);
		TimePeriod firstMondayTillNow = new Date(year, 1, firstMonday).getPeriodTo(this);

		int weeks = -1;
		if (firstMonday == 1) {
			weeks = firstMondayTillNow.toWeeks() + 1;
		} else {
			java.util.Date jFirstMondayDate = new Date(year, 1, firstMonday).toJavaDate();
			java.util.Date jThis = toJavaDate();
			if (jThis.before(jFirstMondayDate)) {
				weeks = 1;
			} else if (jThis.after(jFirstMondayDate)) {
				weeks = firstMondayTillNow.toWeeks() + (firstMondayTillNow.toDays() % 7 >= 0 ? 2 : 1);
			} else {
				weeks = 2;
			}
		}

		return weeks;
	}

	public TimePeriod getPeriodTo(Date other) {
		return new TimePeriod(other.toMillis() - toMillis());
	}

	public static Date today() {
		return new Date();
	}

}
