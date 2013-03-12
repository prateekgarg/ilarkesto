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
package ilarkesto.integration.hochschulkompass;

import ilarkesto.json.AJsonWrapper;
import ilarkesto.json.JsonObject;

import java.util.Collection;
import java.util.List;

public class Values extends AJsonWrapper {

	public Values(JsonObject json) {
		super(json);
	}

	public List<Subjectgroup> getSubjectgroups() {
		return getWrapperArray("subjectgroups", Subjectgroup.class);
	}

	void setSubjectgroups(List<Subjectgroup> subjectgroups) {
		json.put("subjectgroups", subjectgroups);
	}

	public Subjectgroup getSubjectGroupByKey(String subjectgroupKey) {
		return findByKey(getSubjectgroups(), subjectgroupKey);
	}

	public List<Subject> getSubjects() {
		return getWrapperArray("subjects", Subject.class);
	}

	void setSubjects(List<Subject> subjects) {
		json.put("subjects", subjects);
	}

	private <V extends Value> V findByKey(Collection<V> values, String key) {
		if (key == null) return null;
		for (V value : values) {
			if (value.isKey(key)) return value;
		}
		return null;
	}

}
