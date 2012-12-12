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
import java.util.LinkedList;
import java.util.List;

public class Section extends AJsonWrapper {

	public Section(JsonObject json) {
		super(json);
	}

	public Section(String title) {
		putMandatory("title", title);
	}

	public boolean isTop() {
		JsonObject parent = json.getParent();
		if (parent == null) return true;
		return parent.contains("ref");
	}

	public Section getParentSection() {
		if (isTop()) return null;
		return getParent(Section.class);
	}

	public Book getBook() {
		Section parentSection = getParentSection();
		if (parentSection != null) return parentSection.getBook();
		return getParent(Book.class);
	}

	public List<Section> getSectionPath() {
		LinkedList<Section> ret = new LinkedList<Section>();
		Section section = this;
		while (section != null) {
			ret.addFirst(section);
			section = section.getParentSection();
		}
		return ret;
	}

	public String getTitle() {
		return getMandatoryString("title");
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

	public List<Section> getSections() {
		return createFromArray("sections", Section.class);
	}

	public void addSection(Section section) {
		json.addToArray("sections", section);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getBook().getRef().getCode());
		for (Section section : getSectionPath()) {
			sb.append(" > ");
			sb.append(section.getTitle());
		}
		return sb.toString();
	}

}
