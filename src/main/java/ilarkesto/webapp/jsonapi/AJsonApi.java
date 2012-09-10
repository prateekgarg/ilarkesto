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
package ilarkesto.webapp.jsonapi;

import ilarkesto.json.JsonObject;
import ilarkesto.webapp.RequestWrapper;


public abstract class AJsonApi {

	protected void onGet(JsonObject json) {}

	protected void onGet(JsonObject json, RequestWrapper request) {
		onGet(json);
	}

	protected void onPost(JsonObject json) {
		throw new RuntimeException("POST not supported");
	}

	protected void onPost(JsonObject json, RequestWrapper request) {
		onPost(json);
	}

	public final JsonObject get(RequestWrapper request) {
		JsonObject json = new JsonObject();
		onGet(json, request);
		return json;
	}

	public final void post(JsonObject update, RequestWrapper request) {
		onPost(update, request);
	}

}
