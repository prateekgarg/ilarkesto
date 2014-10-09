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
package ilarkesto.core.time;

import ilarkesto.testng.ATest;

import org.testng.annotations.Test;

public class DateRangeTest extends ATest {

	@Test
	public void getDayCount() {
		assertEquals(new DateRange("2014-01-01 - 2014-01-01").getDayCount(), 1);
		assertEquals(new DateRange("2014-01-01 - 2014-01-02").getDayCount(), 2);
	}

	@Test
	public void getTimePeriodBetweenStartAndEnd() {
		assertEquals(new DateRange("2014-01-01 - 2014-01-01").getTimePeriodBetweenStartAndEnd(), new TimePeriod(0));
		assertEquals(new DateRange("2014-01-01 - 2014-01-02").getTimePeriodBetweenStartAndEnd(), new TimePeriod(
				Tm.HOUR * 24));
	}

	@Test
	public void constructionAndToString() {
		assertEquals(new DateRange(new Date(2014, 1, 1), new Date(2015, 1, 1)).toString(), new DateRange(
				"2014-01-01 - 2015-01-01").toString());
	}

	@Test
	public void checkFail() {
		assertCheckFail("2000-01-02 - 2000-01-01");
		assertCheckFail("2000-01-01");
	}

	@Test
	public void checkOk() {
		new DateRange("2000-01-01 - 2000-01-01");
	}

	private void assertCheckFail(String s) {
		try {
			new DateRange(s);
			fail("Exception expected: " + s);
		} catch (IllegalArgumentException ex) {
			// expected
		}
	}

}
