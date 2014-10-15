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
package ilarkesto.integration.infodoc;

import ilarkesto.testng.ATest;

import org.testng.annotations.Test;

public class AItemCounterTest extends ATest {

	@Test
	public void level0() {
		AItemCounter counter = AItemCounter.get(0);
		assertEquals(counter.getNumber(0), "A)");
		assertEquals(counter.getNumber(1), "B)");
		assertEquals(counter.getNumber(2), "C)");
	}

	@Test
	public void level1() {
		AItemCounter counter = AItemCounter.get(1);
		assertEquals(counter.getNumber(1 - 1), "I.");
		assertEquals(counter.getNumber(2 - 1), "II.");
		assertEquals(counter.getNumber(3 - 1), "III.");
		assertEquals(counter.getNumber(10 - 1), "X.");
		assertEquals(counter.getNumber(11 - 1), "XI.");
		assertEquals(counter.getNumber(20 - 1), "XX.");
		assertEquals(counter.getNumber(21 - 1), "XXI.");
	}

	@Test
	public void level2() {
		AItemCounter counter = AItemCounter.get(2);
		assertEquals(counter.getNumber(1 - 1), "1.");
		assertEquals(counter.getNumber(2 - 1), "2.");
		assertEquals(counter.getNumber(3 - 1), "3.");
		assertEquals(counter.getNumber(10 - 1), "10.");
	}

	@Test
	public void level3() {
		AItemCounter counter = AItemCounter.get(3);
		assertEquals(counter.getNumber(0), "a)");
		assertEquals(counter.getNumber(1), "b)");
		assertEquals(counter.getNumber(2), "c)");
	}

	@Test
	public void level4() {
		AItemCounter counter = AItemCounter.get(4);
		assertEquals(counter.getNumber(0), "aa)");
		assertEquals(counter.getNumber(1), "bb)");
		assertEquals(counter.getNumber(2), "cc)");
	}

	@Test
	public void level5() {
		AItemCounter counter = AItemCounter.get(5);
		assertEquals(counter.getNumber(0), "(1)");
		assertEquals(counter.getNumber(1), "(2)");
		assertEquals(counter.getNumber(2), "(3)");
	}

}
