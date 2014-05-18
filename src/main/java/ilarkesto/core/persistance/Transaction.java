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

import ilarkesto.core.base.Str;
import ilarkesto.core.logging.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Transaction {

	private static final Log log = Log.get(Transaction.class);

	private String name;
	private boolean autoCommit;
	private AEntityDatabase backend;

	private EntityCache modified = new EntityCache();
	private Map<String, Map<String, Object>> modifiedPropertiesByEntityId = new HashMap<String, Map<String, Object>>();
	private Set<String> deleted = new HashSet<String>();

	public Transaction(AEntityDatabase backend, String name, boolean autoCommit) {
		super();
		this.backend = backend;
		this.name = name;
		this.autoCommit = autoCommit;
	}

	public void commit() {
		log.info("commit()", toString());
		ensureIntegrity();
		backend.update(modified.getAll(), deleted, modifiedPropertiesByEntityId);
		backend.onTransactionFinished(this);
		modified = null;
		deleted = null;
	}

	private void ensureIntegrity() {
		for (AEntity entity : modified.getAll()) {
			entity.ensureIntegrity();
		}
	}

	public void rollback() {
		log.info("rollback()", toString());
		backend.onTransactionFinished(this);
		modified = null;
		deleted = null;
	}

	public void persist(AEntity entity) {
		String id = entity.getId();
		if (modified.contains(id))
			throw new IllegalStateException("Persisting " + Str.getSimpleName(entity.getClass()) + " with id " + id
					+ " failed. Entity already persisted in this transaction: " + entity);
		if (backend.contains(id))
			throw new IllegalStateException("Persisting " + Str.getSimpleName(entity.getClass()) + " with id" + id
					+ " failed. Entity already exists: " + entity);
		if (autoCommit) {
			backend.update(Arrays.asList(entity), null, updatePropertiesMap(modifiedPropertiesByEntityId, entity));
			return;
		}
		modified.add(entity);
		updatePropertiesMap(modifiedPropertiesByEntityId, entity);
	}

	public void modified(AEntity entity, String field, Object value) {
		if (!contains(entity.getId())) return;
		if (autoCommit) {
			backend.update(Arrays.asList(entity), null, updatePropertiesMap(null, entity, field, value));
			return;
		}
		modified.add(entity);
		updatePropertiesMap(modifiedPropertiesByEntityId, entity, field, value);
	}

	public void delete(String entityId) {
		if (autoCommit) {
			backend.update(null, Arrays.asList(entityId), null);
			return;
		}
		deleted.add(entityId);
		modified.remove(entityId);
	}

	public boolean contains(String id) {
		if (deleted.contains(id)) return false;
		return modified.contains(id) || backend.contains(id);
	}

	public AEntity get(String id) {
		if (deleted.contains(id)) throw new EntityDoesNotExistException(id);
		if (modified.contains(id)) return modified.get(id);
		return backend.get(id);
	}

	public List<AEntity> list(Collection<String> ids) {
		List<AEntity> ret = new ArrayList<AEntity>(ids.size());
		for (String id : ids) {
			ret.add(get(id));
		}
		return ret;
	}

	public AEntity get(AEntityQuery query) {
		// TODO skip deleted
		AEntity entity = modified.get(query);
		if (entity != null) return entity;
		return backend.get(query);
	}

	public List<AEntity> list(AEntityQuery query) {
		// TODO skip deleted
		List<AEntity> ret = backend.list(query);
		for (AEntity entity : modified.list(query)) {
			if (!ret.contains(entity)) ret.add(entity);
		}
		return ret;
	}

	@Override
	public synchronized String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		// if (!entitiesToSave.isEmpty()) {
		// sb.append("\n    SAVE: ").append(toString(entitiesToSave));
		// }
		// if (!entitiesRegistered.isEmpty()) {
		// sb.append("\n    REGISTERED: ").append(toString(entitiesRegistered));
		// }
		// if (!entitiesToDelete.isEmpty()) {
		// sb.append("\n    DELETE: ").append(toString(entitiesToDelete));
		// }
		return sb.toString();
	}

	private static Map<String, Map<String, Object>> updatePropertiesMap(
			Map<String, Map<String, Object>> modifiedPropertiesByEntityId, AEntity entity, String field, Object value) {
		if (modifiedPropertiesByEntityId == null)
			modifiedPropertiesByEntityId = new HashMap<String, Map<String, Object>>();
		String id = entity.getId();
		Map<String, Object> properties = modifiedPropertiesByEntityId.get(id);
		if (properties == null) {
			properties = new HashMap<String, Object>();
			properties.put("id", id);
			modifiedPropertiesByEntityId.put(id, properties);
		}
		properties.put(field, value);
		return modifiedPropertiesByEntityId;
	}

	private static Map<String, Map<String, Object>> updatePropertiesMap(
			Map<String, Map<String, Object>> modifiedPropertiesByEntityId, AEntity entity) {
		if (modifiedPropertiesByEntityId == null)
			modifiedPropertiesByEntityId = new HashMap<String, Map<String, Object>>();
		String id = entity.getId();
		Map<String, Object> properties = entity.createPropertiesMap();
		modifiedPropertiesByEntityId.put(id, properties);
		return modifiedPropertiesByEntityId;
	}

	public static Transaction get() {
		return AEntityDatabase.get().getTransaction();
	}

}
