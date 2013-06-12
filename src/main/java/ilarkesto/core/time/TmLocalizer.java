package ilarkesto.core.time;

public class TmLocalizer {

	public String shortestPeriod(long millis) {
		StringBuilder sb = new StringBuilder();
		long m = millis >= 0 ? millis : -millis;
		if (m >= (Tm.YEAR * 2)) {
			int i = Tm.toYears(millis);
			sb.append(i);
			sb.append(" ").append(years(i));
		} else if (m >= (Tm.MONTH * 2)) {
			int i = Tm.toMonths(millis);
			sb.append(i);
			sb.append(" ").append(months(i));
		} else if (m >= (Tm.WEEK * 2)) {
			int i = Tm.toWeeks(millis);
			sb.append(i);
			sb.append(" ").append(weeks(i));
		} else if (m >= Tm.DAY) {
			int i = Tm.toDays(millis);
			sb.append(i);
			sb.append(" ").append(days(i));
		} else if (m >= ((Tm.HOUR * 2) - (Tm.MINUTE - 20))) {
			long l = Tm.toHours(millis);
			sb.append(l);
			sb.append(" ").append(hours(l));
		} else if (m >= Tm.MINUTE) {
			long l = Tm.toMinutes(millis);
			sb.append(l);
			sb.append(" ").append(minutes(l));
		} else if (m >= Tm.SECOND) {
			long l = Tm.toSeconds(millis);
			sb.append(l);
			sb.append(" ").append(seconds(l));
		} else {
			sb.append(m);
			sb.append(" ").append(millis(m));
		}
		return sb.toString();
	}

	public String full(Weekday day) {
		switch (day) {
			case MONDAY:
				return "Monday";
			case TUESDAY:
				return "Tuesday";
			case WEDNESDAY:
				return "Wednesday";
			case THURSDAY:
				return "Thursday";
			case FRIDAY:
				return "Friday";
			case SATURDAY:
				return "Saturday";
			case SUNDAY:
				return "Sunday";
		}
		throw new IllegalStateException(day.name());
	}

	public String shorted(Weekday day) {
		switch (day) {
			case MONDAY:
				return "Mon";
			case TUESDAY:
				return "Tue";
			case WEDNESDAY:
				return "Wed";
			case THURSDAY:
				return "Thu";
			case FRIDAY:
				return "Fri";
			case SATURDAY:
				return "Sat";
			case SUNDAY:
				return "Sun";
		}
		throw new IllegalStateException(day.name());
	}

	public String full(Month month) {
		switch (month) {
			case JANUARY:
				return "January";
			case FEBRUARY:
				return "February";
			case MARCH:
				return "March";
			case APRIL:
				return "April";
			case MAY:
				return "May";
			case JUNE:
				return "June";
			case JULY:
				return "July";
			case AUGUST:
				return "August";
			case SEPTEMBER:
				return "September";
			case OCTOBER:
				return "October";
			case NOVEMBER:
				return "November";
			case DECEMBER:
				return "December";
		}
		throw new IllegalArgumentException(month.name());
	}

	public String years(long count) {
		return count == 1 || count == -1 ? "year" : "years";
	}

	public String months(long count) {
		return count == 1 || count == -1 ? "month" : "months";
	}

	public String weeks(long count) {
		return count == 1 || count == -1 ? "week" : "weeks";
	}

	public String days(long count) {
		return count == 1 || count == -1 ? "day" : "days";
	}

	public String hours(long count) {
		return count == 1 || count == -1 ? "hour" : "hours";
	}

	public String minutes(long count) {
		return count == 1 || count == -1 ? "minute" : "minutes";
	}

	public String seconds(long count) {
		return count == 1 || count == -1 ? "second" : "seconds";
	}

	public String millis(long count) {
		return count == 1 || count == -1 ? "milli" : "millis";
	}

}
