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
package ilarkesto.core.base;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapBuilder<K, V> {

	private Map<K, V> map;

	public MapBuilder(Map<K, V> map) {
		super();
		this.map = map;
	}

	public MapBuilder(boolean keepOrder) {
		this(keepOrder ? new LinkedHashMap<K, V>() : new HashMap<K, V>());
	}

	public MapBuilder() {
		this(false);
	}

	public MapBuilder<K, V> put(K key, V value) {
		map.put(key, value);
		return this;
	}

	public Map<K, V> getMap() {
		return map;
	}

	@Override
	public String toString() {
		return Str.format(map);
	}

}
