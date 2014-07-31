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
package ilarkesto.core.localization;

import ilarkesto.testng.ATest;

import java.math.BigDecimal;

import org.testng.annotations.Test;

public class LocalizerTest extends ATest {

	@Test
	public void formatDecimal() {
		Localizer l = new Localizer();

		assertEquals(l.format(new BigDecimal("1000.123"), false), "1000.123");
		assertEquals(l.format(new BigDecimal("1000.123"), true), "1,000.123");
		assertEquals(l.format(new BigDecimal("2111000.123"), true), "2,111,000.123");
		assertEquals(l.format(new BigDecimal("3222111000.123"), true), "3,222,111,000.123");
	}

}
