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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AEntitySetBackReferenceHelper<E extends AEntity> {

	private Map<String, Set<E>> cachesById = new HashMap<String, Set<E>>();

	protected abstract Set<E> loadById(String id);

	public synchronized Set<E> getById(String id) {
		if (AEntityDatabase.instance.isPartial()) return loadById(id);
		Set<E> cache = cachesById.get(id);
		if (cache == null) {
			cache = loadById(id);
			cachesById.put(id, cache);
		}
		if (!cache.isEmpty()) removeDeleted(cache);
		return cache;
	}

	private void removeDeleted(Set<E> cache) {
		Iterator<E> iterator = cache.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().isDeleted()) iterator.remove();
		}
	}

	public synchronized void clear(String id) {
		cachesById.remove(id);
	}

}
