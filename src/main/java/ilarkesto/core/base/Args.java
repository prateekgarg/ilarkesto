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

public class Args {

	public static void assertNotBlank(String arg, String name) {
		if (Str.isBlank(name)) throw new IllegalArgumentException("Argument must not be blank: " + name);
	}

	public static void assertNotNull(Object arg, String name) {
		if (arg == null) throw new IllegalArgumentException("Argument must not be null: " + name);
	}

	public static void assertNotNull(Object arg1, String name1, Object arg2, String name2) {
		assertNotNull(arg1, name1);
		assertNotNull(arg2, name2);
	}

	public static void assertNotNull(Object arg1, String name1, Object arg2, String name2, Object arg3, String name3) {
		assertNotNull(arg1, name1);
		assertNotNull(arg2, name2);
		assertNotNull(arg3, name3);
	}

}
