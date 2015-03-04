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
import java.util.Map;
import java.util.Set;

public abstract class ACachingEntityDatabase extends AEntityDatabase {

	protected EntityCache cache = new EntityCache();

	protected abstract void onUpdate(Collection<AEntity> entities, Collection<String> entityIds,
			Map<String, Map<String, String>> modifiedPropertiesByEntityId, Runnable callback);

	@Override
	public synchronized void update(Collection<AEntity> modified, Collection<String> deletedIds,
			Map<String, Map<String, String>> modifiedPropertiesByEntityId, Runnable callback) {
		onUpdate(modified, deletedIds, modifiedPropertiesByEntityId, callback);
		cache.addAll(modified);
		cache.removeAll(deletedIds);
	}

	@Override
	public synchronized AEntity get(String id) throws EntityDoesNotExistException {
		return cache.get(id);
	}

	@Override
	public synchronized boolean contains(String id) {
		return cache.contains(id);
	}

	@Override
	public synchronized Set<AEntity> list(Collection<String> ids) throws EntityDoesNotExistException {
		return cache.list(ids);
	}

	@Override
	public AEntity get(AEntityQuery query) {
		return cache.get(query);
	}

	@Override
	public Set<AEntity> list(AEntityQuery query) {
		return cache.list(query);
	}

	@Override
	public Collection<AEntity> listAll() {
		return cache.getAll();
	}

}
