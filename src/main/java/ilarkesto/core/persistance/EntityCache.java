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

public class EntityCache<E extends Entity> {

	private Map<Class, Map<String, E>> entitiesByTypeById = new HashMap<Class, Map<String, E>>();

	public Collection<E> getAll() {
		ArrayList<E> ret = new ArrayList<E>();
		for (Map<String, E> entitiesById : entitiesByTypeById.values()) {
			ret.addAll(entitiesById.values());
		}
		return ret;
	}

	public Set<String> getAllIds() {
		Set<String> ret = new HashSet<String>();
		for (Map<String, E> entitiesById : entitiesByTypeById.values()) {
			ret.addAll(entitiesById.keySet());
		}
		return ret;
	}

	public Set<E> list(AEntityQuery query) {
		Class queryType = query.getType();

		Set<E> ret = new HashSet<E>();
		for (Entry<Class, Map<String, E>> entry : entitiesByTypeById.entrySet()) {
			if (queryType != null) {
				Class type = entry.getKey();
				if (!isInstanceOf(type, queryType)) continue;
			}
			Map<String, E> entitiesById = entry.getValue();
			for (E entity : entitiesById.values()) {
				if (query.test(entity)) ret.add(entity);
			}
		}

		return ret;
	}

	public E get(AEntityQuery query) {
		Class queryType = query.getType();

		for (Entry<Class, Map<String, E>> entry : entitiesByTypeById.entrySet()) {
			if (queryType != null) {
				Class type = entry.getKey();
				if (!isInstanceOf(type, queryType)) continue;
			}
			Map<String, E> entitiesById = entry.getValue();
			for (E entity : entitiesById.values()) {
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

	public void add(E entity) {
		Class type = entity.getClass();
		Map<String, E> entitiesById = entitiesByTypeById.get(type);
		if (entitiesById == null) {
			entitiesById = new HashMap<String, E>();
			entitiesByTypeById.put(type, entitiesById);
		}
		entitiesById.put(entity.getId(), entity);
	}

	public void addAll(Collection<E> entities) {
		if (entities == null) return;
		for (E entity : entities) {
			add(entity);
		}
	}

	public E remove(String entityId) {
		if (entityId == null) return null;
		for (Map<String, E> entitiesById : entitiesByTypeById.values()) {
			E removed = entitiesById.remove(entityId);
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
		for (Map<String, E> entitiesById : entitiesByTypeById.values()) {
			if (entitiesById.containsKey(id)) return true;
		}
		return false;
	}

	public E get(String id) throws EntityDoesNotExistException {
		E entity = null;
		for (Map<String, E> entitiesById : entitiesByTypeById.values()) {
			entity = entitiesById.get(id);
			if (entity != null) break;
		}
		if (entity == null) throw new EntityDoesNotExistException(id);
		return entity;
	}

	public Set<E> list(Collection<String> ids) throws EntityDoesNotExistException {
		Set<E> ret = new HashSet<E>();
		for (String id : ids) {
			ret.add(get(id));
		}
		return ret;
	}

	public int size() {
		int sum = 0;
		for (Map<String, E> entitiesById : entitiesByTypeById.values()) {
			sum += entitiesById.size();
		}
		return sum;
	}

	public boolean isEmpty() {
		for (Map<String, E> entitiesById : entitiesByTypeById.values()) {
			if (!entitiesById.isEmpty()) return false;
		}
		return true;
	}

	public final Map<Class, Integer> countEntities() {
		Map<Class, Integer> countsByType = new HashMap<Class, Integer>();

		for (Entry<Class, Map<String, E>> entry : entitiesByTypeById.entrySet()) {
			countsByType.put(entry.getKey(), entry.getValue().size());
		}

		return countsByType;
	}

}
