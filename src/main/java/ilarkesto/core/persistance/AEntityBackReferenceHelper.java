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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AEntityBackReferenceHelper<E extends AEntity> {

	private Map<String, String> cachesById = new HashMap<String, String>();

	protected abstract E loadById(String id);

	public synchronized E getById(String id) {
		// if (AEntityDatabase.instance.isPartial()) return loadById(id);
		String cache = cachesById.get(id);

		if (cache != null) {
			try {
				return (E) AEntity.getById(cache);
			} catch (EntityDoesNotExistException ex) {}
		}

		E entity = loadById(id);
		if (entity == null) return null;
		if (!AEntityDatabase.instance.isTransactionWithChangesOpen()) {
			cachesById.put(id, entity.getId());
		}
		return entity;
	}

	public synchronized void clear(String id) {
		cachesById.remove(id);
	}

	public synchronized void clear(Collection<String> ids) {
		for (String id : ids) {
			clear(id);
		}
	}

}
