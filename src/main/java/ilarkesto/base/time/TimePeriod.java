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

import ilarkesto.base.Tm;

public final class TimePeriod extends ilarkesto.core.time.TimePeriod {

	public static final TimePeriod ZERO = new TimePeriod(0);

	public TimePeriod(long millis) {
		super(millis);
	}

	public TimePeriod(String s) {
		super(s);
	}

	@Override
	protected TimePeriod newTimePeriod(long millis) {
		return new TimePeriod(millis);
	}

	// ---

	@Override
	public TimePeriod multiplyBy(int factor) {
		return (TimePeriod) super.multiplyBy(factor);
	}

	@Override
	public TimePeriod divide(int divisor) {
		return (TimePeriod) super.divide(divisor);
	}

	@Override
	public TimePeriod subtract(ilarkesto.core.time.TimePeriod tp) {
		return (TimePeriod) super.subtract(tp);
	}

	@Override
	public TimePeriod add(ilarkesto.core.time.TimePeriod tp) {
		return (TimePeriod) super.add(tp);
	}

	public TimePeriod subtract(Time time) {
		return new TimePeriod(toMillis() - time.toMillis());
	}

	public String toHoursAndMinutesString() {
		long hours = toHours();
		long minutes = toMinutes() - (hours * 60);
		StringBuilder sb = new StringBuilder();
		sb.append(hours);
		sb.append(':');
		if (minutes < 10) sb.append('0');
		sb.append(minutes);
		return sb.toString();
	}

	public static TimePeriod seconds(int seconds) {
		return new TimePeriod(seconds * Tm.SECOND);
	}

	public static TimePeriod minutes(int minutes) {
		return new TimePeriod(minutes * Tm.MINUTE);
	}

	public static TimePeriod hours(int hours) {
		return new TimePeriod(hours * Tm.HOUR);
	}

	public static TimePeriod days(int days) {
		return new TimePeriod(days * Tm.DAY);
	}

	public static TimePeriod weeks(int weeks) {
		return new TimePeriod(weeks * Tm.WEEK);
	}

}
