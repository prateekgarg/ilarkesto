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
package ilarkesto.webapp.restapi;

import ilarkesto.json.JsonObject;

import java.util.Map;

public abstract class ARestApi {

	protected void onGet(JsonObject json) {}

	protected void onGet(JsonObject json, Map<String, String> parameters) {
		onGet(json);
	}

	protected void onPost(JsonObject json) {
		throw new RuntimeException("POST not supported");
	}

	protected void onPost(JsonObject json, Map<String, String> parameters) {
		onPost(json);
	}

	public final JsonObject get(Map<String, String> parameters) {
		JsonObject json = new JsonObject();
		onGet(json, parameters);
		return json;
	}

	public final void post(JsonObject update, Map<String, String> parameters) {
		onPost(update, parameters);
	}

}
