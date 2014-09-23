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
package ilarkesto.core.base;

import ilarkesto.testng.ATest;

import java.util.List;

import org.testng.annotations.Test;

public class StrTest extends ATest {

	@Test
	public void parseCommaSeparatedString() {
		List<String> ret = Str.parseCommaSeparatedString("a,b, c", true);
		assertSize(ret, 3);
		assertEquals(ret.get(0), "a");
		assertEquals(ret.get(1), "b");
		assertEquals(ret.get(2), "c");
	}

	@Test
	public void isEmail() {
		assertTrue(Str.isEmail("wi@koczewski.de"));
		assertTrue(Str.isEmail("123@123.com"));
		assertTrue(Str.isEmail("stöckle@nesselwang.de"));
	}

}
