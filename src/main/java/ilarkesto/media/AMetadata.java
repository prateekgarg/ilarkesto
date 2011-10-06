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
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.media;

import ilarkesto.core.base.Str;

import java.util.HashMap;
import java.util.Map;

public abstract class AMetadata {

	private Map<String, String> data = new HashMap<String, String>();

	public abstract String getFullTitle();

	protected String get(String key) {
		return data.get(key);
	}

	protected void set(String key, String value) {
		if (Str.isBlank(value)) {
			data.remove(key);
		} else {
			data.put(key, value);
		}
	}

	protected Integer getInteger(String key) {
		String s = get(key);
		return s == null ? null : Integer.parseInt(s);
	}

	@Override
	public String toString() {
		return getFullTitle();
	}

}
