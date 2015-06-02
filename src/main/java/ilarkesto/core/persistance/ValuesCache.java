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
package ilarkesto.core.persistance;

import java.util.HashMap;
import java.util.Map;

public class ValuesCache {

	private Map<String, Object> valuesByKey = new HashMap<String, Object>();

	public Object get(String name, Object... keyParts) {
		String key = createKey(name, keyParts);
		return valuesByKey.get(key);
	}

	public <T> T put(T value, String name, Object... keyParts) {
		String key = createKey(name, keyParts);
		valuesByKey.put(key, value);
		return value;
	}

	private String createKey(String name, Object... keyParts) {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		for (Object keyPart : keyParts) {
			if (keyPart instanceof AEntity) {
				sb.append(((AEntity) keyPart).getId());
			} else {
				sb.append(keyPart);
			}
		}
		return sb.toString();
	}

}
