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

import java.util.Collection;
import java.util.Map;

public class EnumMapperForMap implements EnumMapper<String, String> {

	private Map<String, String> map;

	public EnumMapperForMap(Map<String, String> map) {
		super();
		this.map = map;
	}

	@Override
	public Collection<String> getKeys() {
		return map.keySet();
	}

	@Override
	public String getValueForKey(String key) {
		return map.get(key);
	}
}
