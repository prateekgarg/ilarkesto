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
package ilarkesto.integration.fuel;

import ilarkesto.json.AJsonWrapper;

public class Fuel extends AJsonWrapper {

	public static final String ID_DIESEL = "DIESEL";
	public static final String ID_E5 = "E5";
	public static final String ID_E10 = "E10";

	public String getId() {
		return json.getString("id");
	}

	public String getLabel() {
		return getId();
	}

	public Price getLatestPrice() {
		return getWrapper("latestPrice", Price.class);
	}

	public void addPrice(Price price) {
		throw new RuntimeException("not implemented yet");
	}

	public static class Price extends AJsonWrapper {

	}

}
