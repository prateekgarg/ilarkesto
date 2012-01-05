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
package ilarkesto.ui.web.jqm;

// http://jquerymobile.com/demos/1.0/docs/buttons/buttons-icons.html
public enum DataIcon {

	Delete("delete"), ArrowLeft("arrow-l"), ArrowRight("arrow-r"), ArrowUp("arrow-u"), ArrowDown("arrow-d"), Plus(
			"plus"), Minus("minus"), Check("check"), Gear("gear"), Refresh("refresh"), Forward("forward"), Back("back"), Grid(
			"grid"), Star("star"), Alert("alert"), Info("info"), Home("home"), Search("search");

	private String name;

	DataIcon(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

}
