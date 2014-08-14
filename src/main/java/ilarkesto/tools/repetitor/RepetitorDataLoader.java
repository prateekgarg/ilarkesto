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
package ilarkesto.tools.repetitor;

import ilarkesto.json.JsonObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RepetitorDataLoader {

	public static void main(String[] args) {
		Collection<JsonObject> subjects = loadAllSubjects();
		for (JsonObject jSubject : subjects) {
			System.out.println(jSubject.toFormatedString());
			// jSubject.write(new File("../repetitor/android-app/src/" + subject + ".json"), true);
		}
	}

	public static Collection<JsonObject> loadAllSubjects() {
		JsonObject jResult = JsonObject
				.loadFromUrl("https://servisto.de/couchdb/repetitor/_all_docs?include_docs=true");

		String[] subjects = new String[] { "gg", "VerwR", "test", "vwgo", "stgb" };
		Map<String, JsonObject> ret = new HashMap<String, JsonObject>();
		for (String subject : subjects) {
			JsonObject json = new JsonObject();
			json.put("id", subject);
			ret.put(subject, json);
		}

		for (JsonObject jRow : jResult.getArrayOfObjects("rows")) {
			JsonObject jDoc = jRow.getObject("doc");
			String id = jDoc.getString("_id");
			for (String subject : subjects) {
				JsonObject jSubject = ret.get(subject);
				if (id.startsWith(subject)) {
					String type = jDoc.getString("type");
					jDoc.rename("_id", "id");
					jDoc.remove("_rev");
					if (type.equals("definition")) {
						jSubject.addToArray("definitions", jDoc);
					} else if (type.equals("structure")) {
						jSubject.addToArray("structures", jDoc);
					} else {
						throw new RuntimeException("Unsupported type: " + type + " -> " + jDoc.toFormatedString());
					}
				}
			}
		}

		return ret.values();
	}

}
