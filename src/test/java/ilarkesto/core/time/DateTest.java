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

import ilarkesto.testng.ATest;

import org.testng.annotations.Test;

public class DateTest extends ATest {

	private static final Date BIRTHDAY = new Date(1979, 8, 3);
	private static final Date ARMAGEDDON = new Date(2012, 12, 21);

	@Test
	public void isToday() {
		assertTrue(new Date().isToday());
		assertFalse(BIRTHDAY.isToday());
	}

	@Test
	public void isBetween() {
		assertTrue(BIRTHDAY.isBetween(new Date(1979, 8, 2), new Date(1979, 8, 4), true));

		assertTrue(BIRTHDAY.isBetween(new Date(1979, 8, 3), new Date(1979, 8, 3), true));
		assertFalse(BIRTHDAY.isBetween(new Date(1979, 8, 3), new Date(1979, 8, 3), false));
	}

	@Test
	public void isBefore() {
		assertTrue(BIRTHDAY.isBefore(new Date(1979, 8, 4)));
		assertFalse(BIRTHDAY.isBefore(new Date(1979, 8, 2)));
		assertFalse(BIRTHDAY.isBefore(new Date(1979, 8, 3)));
	}

	@Test
	public void isAfter() {
		assertTrue(BIRTHDAY.isAfter(new Date(1979, 8, 2)));
		assertFalse(BIRTHDAY.isAfter(new Date(1979, 8, 4)));
		assertFalse(BIRTHDAY.isAfter(new Date(1979, 8, 3)));
	}
}
