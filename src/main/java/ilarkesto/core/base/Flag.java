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

public class Flag {

	private boolean set;

	public boolean isSet() {
		return set;
	}

	public void setSet(boolean set) {
		this.set = set;
	}

	public void on() {
		set = true;
	}

	public void off() {
		set = false;
	}

	@Override
	public int hashCode() {
		return Boolean.valueOf(set).hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Flag)) return false;
		return ((Flag) o).set == set;
	}

	@Override
	public String toString() {
		return String.valueOf(set);
	}

}
