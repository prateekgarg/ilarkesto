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
package ilarkesto.law;

import ilarkesto.json.AJsonWrapper;
import ilarkesto.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Book extends AJsonWrapper {

	public Book(JsonObject json) {
		super(json);
	}

	public Book(BookRef ref) {
		json.put("ref", ref);
	}

	public BookRef getRef() {
		return createFromObject("ref", BookRef.class);
	}

	public List<Section> getSections() {
		return createFromArray("sections", Section.class);
	}

	public void addSection(Section section) {
		json.addToArray("sections", section);
	}

	public List<Norm> getNorms() {
		List<Norm> ret = new ArrayList<Norm>();
		ret.addAll(createFromArray("norms", Norm.class));
		for (Section section : getSections()) {
			ret.addAll(section.getNorms());
		}
		return ret;
	}

	public void addNorm(Norm norm) {
		json.addToArray("norms", norm);
	}

	@Override
	public String toString() {
		return getRef().toString();
	}

}
