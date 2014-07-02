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
package ilarkesto.core.base;

import ilarkesto.testng.ATest;

import java.util.List;

import org.testng.annotations.Test;

public class UtlTest extends ATest {

	@Test
	public void moveElementUp() {
		assertEquals(Utl.moveElementUp(Utl.arrayList("a", "b", "c"), "b"), Utl.arrayList("b", "a", "c"));
		assertEquals(Utl.moveElementUp(Utl.arrayList("a", "b", "c"), "a"), Utl.arrayList("a", "b", "c"));
		assertEquals(Utl.moveElementUp(Utl.arrayList("a", "c"), "b"), Utl.arrayList("a", "b", "c"));
	}

	@Test
	public void moveElementDown() {
		assertEquals(Utl.moveElementDown(Utl.arrayList("a", "b", "c"), "b"), Utl.arrayList("a", "c", "b"));
		assertEquals(Utl.moveElementDown(Utl.arrayList("a", "b", "c"), "c"), Utl.arrayList("a", "b", "c"));
		assertEquals(Utl.moveElementDown(Utl.arrayList("a", "c"), "b"), Utl.arrayList("a", "c", "b"));
	}

	@Test
	public void equals() {
		Object[] a = { "hello", "equals" };
		Object[] b = { "hello", "equals" };
		Object[] c = { "hello", "world" };
		assertTrue(Utl.equals(a, b));
		assertFalse(Utl.equals(a, c));
	}

	@Test
	public void removeDuplicates() {
		List<String> list = Utl.toList("a", "b", "b", "a");
		Utl.removeDuplicates(list);
		assertEquals(list, Utl.toList("a", "b"));
	}

}
