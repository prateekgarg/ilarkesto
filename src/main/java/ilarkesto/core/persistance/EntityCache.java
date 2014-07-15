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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EntityCache {

	private Map<String, AEntity> entitiesById = new HashMap<String, AEntity>();

	public Collection<AEntity> getAll() {
		return entitiesById.values();
	}

	public Set<String> getAllIds() {
		return entitiesById.keySet();
	}

	public List<AEntity> list(AEntityQuery query) {
		Class queryType = query.getType();
		List<AEntity> ret = new ArrayList<AEntity>();
		for (AEntity entity : entitiesById.values()) {
			if (queryType != null && !queryType.equals(entity.getClass())) continue;
			if (query.matches(entity)) ret.add(entity);
		}
		return ret;
	}

	public AEntity get(AEntityQuery query) {
		Class queryType = query.getType();
		for (AEntity entity : entitiesById.values()) {
			if (queryType != null && !queryType.equals(entity.getClass())) continue;
			if (query.matches(entity)) return entity;
		}
		return null;
	}

	public void add(AEntity entity) {
		entitiesById.put(entity.getId(), entity);
	}

	public void addAll(Collection<AEntity> entities) {
		if (entities == null) return;
		for (AEntity entity : entities) {
			add(entity);
		}
	}

	public void remove(String entityId) {
		if (entityId == null) return;
		entitiesById.remove(entityId);
	}

	public void removeAll(Collection<String> ids) {
		if (ids == null) return;
		for (String id : ids) {
			remove(id);
		}
	}

	public boolean contains(String id) {
		return entitiesById.containsKey(id);
	}

	public AEntity get(String id) throws EntityDoesNotExistException {
		AEntity entity = entitiesById.get(id);
		if (entity == null) throw new EntityDoesNotExistException(id);
		return entity;
	}

	public List<AEntity> list(Collection<String> ids) throws EntityDoesNotExistException {
		List<AEntity> ret = new ArrayList<AEntity>(ids.size());
		for (String id : ids) {
			ret.add(get(id));
		}
		return ret;
	}

	public int size() {
		return entitiesById.size();
	}

	public boolean isEmpty() {
		return entitiesById.isEmpty();
	}

}
