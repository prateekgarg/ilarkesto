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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EntityCache {

	private Map<String, AEntity> entitiesById = new HashMap<String, AEntity>();

	// TODO performance optimization: entitiesByType.byId (inheritence!)

	public Collection<AEntity> getAll() {
		return entitiesById.values();
	}

	public Set<String> getAllIds() {
		return entitiesById.keySet();
	}

	public Set<AEntity> list(AEntityQuery query) {
		Class queryType = query.getType();
		Set<AEntity> ret = new HashSet<AEntity>();
		for (AEntity entity : entitiesById.values()) {
			if (queryType != null && !isInstanceOf(entity.getClass(), queryType)) continue;
			if (query.test(entity)) ret.add(entity);
		}
		return ret;
	}

	public AEntity get(AEntityQuery query) {
		Class queryType = query.getType();
		for (AEntity entity : entitiesById.values()) {
			if (queryType != null && !isInstanceOf(entity.getClass(), queryType)) continue;
			if (query.test(entity)) return entity;
		}
		return null;
	}

	boolean isInstanceOf(Class givenType, Class requiredType) {
		if (requiredType.equals(givenType)) return true;
		Class superType = givenType.getSuperclass();
		if (superType.equals(Object.class)) return false;
		return isInstanceOf(superType, requiredType);
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

	public Set<AEntity> list(Collection<String> ids) throws EntityDoesNotExistException {
		Set<AEntity> ret = new HashSet<AEntity>();
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

	public final Map<Class, Integer> countEntities() {
		Map<Class, Integer> countsByType = new HashMap<Class, Integer>();
		for (AEntity entity : getAll()) {
			Class type = entity.getClass();
			Integer count = countsByType.get(type);
			if (count == null) {
				count = 1;
			} else {
				count = count + 1;
			}
			countsByType.put(type, count);
		}
		return countsByType;
	}

}
