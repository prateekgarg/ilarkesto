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

import ilarkesto.core.base.Str;
import ilarkesto.json.AJsonWrapper;
import ilarkesto.json.JsonObject;

public class Value extends AJsonWrapper {

	public Value(JsonObject json) {
		super(json);
	}

	public Value(String key, String value) {
		if (Str.isBlank(key)) throw new IllegalArgumentException("key == null");
		json.put("key", key.trim());
		if (Str.isBlank(value)) throw new IllegalArgumentException("value == null");
		json.put("value", value.trim());
	}

	public String getKey() {
		return json.getString("key");
	}

	public boolean isKey(String key) {
		return getKey().equals(key);
	}

	public String getValue() {
		return json.getString("value");
	}

	@Override
	public int hashCode() {
		return getKey().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!getClass().equals(obj.getClass())) return false;
		return getKey().equals(((Value) obj).getKey());
	}

	@Override
	public String toString() {
		return getKey() + ": " + getValue();
	}

}
