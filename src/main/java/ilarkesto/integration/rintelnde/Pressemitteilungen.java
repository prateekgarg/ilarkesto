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
package ilarkesto.integration.rintelnde;

import ilarkesto.json.AJsonWrapper;
import ilarkesto.json.JsonObject;

import java.util.List;

public class Pressemitteilungen extends AJsonWrapper {

	public Pressemitteilungen() {}

	public Pressemitteilungen(JsonObject json) {
		super(json);
	}

	public void addMitteilung(Pressemitteilung mitteilung) {
		json.addToArray("mitteilungen", mitteilung);
	}

	public List<Pressemitteilung> getMitteilungen() {
		return getWrapperArray("mitteilungen", Pressemitteilung.class);
	}

	public Pressemitteilung getMitteilungById(int id) {
		for (Pressemitteilung pm : getMitteilungen()) {
			if (pm.getId() == id) return pm;
		}
		return null;
	}

	public boolean containsMitteilung(int id) {
		return getMitteilungById(id) != null;
	}

}
