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
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class EntityCache {

	private Map<Class, Map<String, AEntity>> entitiesByTypeById = new HashMap<Class, Map<String, AEntity>>();

	public Collection<AEntity> getAll() {
		ArrayList<AEntity> ret = new ArrayList<AEntity>();
		for (Map<String, AEntity> entitiesById : entitiesByTypeById.values()) {
			ret.addAll(entitiesById.values());
		}
		return ret;
	}

	public Set<String> getAllIds() {
		Set<String> ret = new HashSet<String>();
		for (Map<String, AEntity> entitiesById : entitiesByTypeById.values()) {
			ret.addAll(entitiesById.keySet());
		}
		return ret;
	}

	public Set<AEntity> list(AEntityQuery query) {
		Class queryType = query.getType();

		Set<AEntity> ret = new HashSet<AEntity>();
		for (Entry<Class, Map<String, AEntity>> entry : entitiesByTypeById.entrySet()) {
			if (queryType != null) {
				Class type = entry.getKey();
				if (!isInstanceOf(type, queryType)) continue;
			}
			Map<String, AEntity> entitiesById = entry.getValue();
			for (AEntity entity : entitiesById.values()) {
				if (query.test(entity)) ret.add(entity);
			}
		}

		return ret;
	}

	public AEntity get(AEntityQuery query) {
		Class queryType = query.getType();

		for (Entry<Class, Map<String, AEntity>> entry : entitiesByTypeById.entrySet()) {
			if (queryType != null) {
				Class type = entry.getKey();
				if (!isInstanceOf(type, queryType)) continue;
			}
			Map<String, AEntity> entitiesById = entry.getValue();
			for (AEntity entity : entitiesById.values()) {
				if (query.test(entity)) return entity;
			}
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
		Class type = entity.getClass();
		Map<String, AEntity> entitiesById = entitiesByTypeById.get(type);
		if (entitiesById == null) {
			entitiesById = new HashMap<String, AEntity>();
			entitiesByTypeById.put(type, entitiesById);
		}
		entitiesById.put(entity.getId(), entity);
	}

	public void addAll(Collection<AEntity> entities) {
		if (entities == null) return;
		for (AEntity entity : entities) {
			add(entity);
		}
	}

	public AEntity remove(String entityId) {
		if (entityId == null) return null;
		for (Map<String, AEntity> entitiesById : entitiesByTypeById.values()) {
			AEntity removed = entitiesById.remove(entityId);
			if (removed != null) return removed;
		}
		return null;
	}

	public void removeAll(Collection<String> ids) {
		if (ids == null) return;
		for (String id : ids) {
			remove(id);
		}
	}

	public boolean contains(String id) {
		for (Map<String, AEntity> entitiesById : entitiesByTypeById.values()) {
			if (entitiesById.containsKey(id)) return true;
		}
		return false;
	}

	public AEntity get(String id) throws EntityDoesNotExistException {
		AEntity entity = null;
		for (Map<String, AEntity> entitiesById : entitiesByTypeById.values()) {
			entity = entitiesById.get(id);
			if (entity != null) break;
		}
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
		int sum = 0;
		for (Map<String, AEntity> entitiesById : entitiesByTypeById.values()) {
			sum += entitiesById.size();
		}
		return sum;
	}

	public boolean isEmpty() {
		for (Map<String, AEntity> entitiesById : entitiesByTypeById.values()) {
			if (!entitiesById.isEmpty()) return false;
		}
		return true;
	}

	public final Map<Class, Integer> countEntities() {
		Map<Class, Integer> countsByType = new HashMap<Class, Integer>();

		for (Entry<Class, Map<String, AEntity>> entry : entitiesByTypeById.entrySet()) {
			countsByType.put(entry.getKey(), entry.getValue().size());
		}

		return countsByType;
	}

}
