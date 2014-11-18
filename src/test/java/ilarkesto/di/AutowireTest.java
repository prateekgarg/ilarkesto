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
package ilarkesto.di;

import ilarkesto.core.scope.In;
import ilarkesto.testng.ATest;

import org.testng.annotations.Test;

public class AutowireTest extends ATest {

	@Test
	public void autowireClass() {
		BeanContainer beans = new BeanContainer();
		beans.put("a", "x");
		beans.put("b", "x");
		beans.put("c", "x");
		beans.put("d", "x");
		Autowire.autowireClass(Rose.class, beans, null);
		// assertEquals(Rose.a, "x");
		assertEquals(Rose.b, "x");
		// assertEquals(Flower.c, "x");
		assertEquals(Flower.d, "x");
	}

	public static class Rose extends Flower {

		@In
		private static String a;

		private static String b;

		public static void setB(String b) {
			Rose.b = b;
		}
	}

	public static class Flower {

		@In
		private static String c;

		private static String d;

		public static void setD(String d) {
			Flower.d = d;
		}
	}
}
