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

import ilarkesto.core.time.Date;
import ilarkesto.json.AJsonWrapper;
import ilarkesto.json.JsonObject;

public class Pressemitteilung extends AJsonWrapper {

	public Pressemitteilung(JsonObject json) {
		super(json);
	}

	public Pressemitteilung(String label, Integer id, Date date) {
		putMandatory("label", label);
		putMandatory("id", id);
		putMandatory("date", date.toString());
	}

	public String getLabel() {
		return json.getString("label");
	}

	public int getId() {
		return json.getInteger("id");
	}

	public Date getDate() {
		return new Date(getMandatoryString("date"));
	}

	void setTextAsHtml(String html) {
		json.put("textAsHtml", html);
	}

	public String getTextAsHtml() {
		return json.getString("textAsHtml");
	}

	@Override
	public String toString() {
		return getDate() + ": " + getLabel();
	}
}
