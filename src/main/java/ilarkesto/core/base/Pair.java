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

public class Pair<A, B> {

	public final A a;
	public final B b;

	public Pair(A a, B b) {
		super();
		this.a = a;
		this.b = b;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		if (a != null) hash = hash + a.hashCode() * 7;
		if (b != null) hash = hash + b.hashCode() * 7;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Pair)) return false;
		return Utl.equals(a, ((Pair) obj).a) && Utl.equals(b, ((Pair) obj).b);
	}

	@Override
	public String toString() {
		return "<" + a + "," + b + ">";
	}

}
