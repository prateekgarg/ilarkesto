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

public class Norm extends AJsonWrapper {

	public Norm(JsonObject json) {
		super(json);
	}

	public Norm(NormRef ref, String title) {
		putMandatory("ref", ref);
		json.put("title", title);
	}

	public String getTextAsString() {
		return "dummy";
	}

	public String getTitle() {
		return json.getString("title");
	}

	public boolean isTitleAvailable() {
		return json.isSet("title");
	}

	public Section getSection() {
		return getParent(Section.class);
	}

	public NormRef getRef() {
		return createFromObject("ref", NormRef.class);
	}

	@Override
	public String toString() {
		return getRef().toString();
	}

}
