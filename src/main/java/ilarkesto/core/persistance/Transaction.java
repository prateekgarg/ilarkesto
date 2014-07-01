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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Transaction {

	private static final Log log = Log.get(Transaction.class);

	private String name;
	private boolean autoCommit;
	private AEntityDatabase backend;
	private boolean ignoreModifications;
	private boolean ensureIntegrityOnCommit;
	private boolean ensuringIntegrity;
	private LinkedList<Runnable> runnablesAfterCommit;

	private EntityCache modified = new EntityCache();
	private Map<String, Map<String, Object>> modifiedPropertiesByEntityId = new HashMap<String, Map<String, Object>>();
	private Set<String> deleted = new HashSet<String>();

	public Transaction(AEntityDatabase backend, String name, boolean autoCommit, boolean ensureIntegrityOnCommit) {
		super();
		this.backend = backend;
		this.name = name;
		this.autoCommit = autoCommit;
		this.ensureIntegrityOnCommit = ensureIntegrityOnCommit;
	}

	public void commit() {
		if (autoCommit) throw new IllegalStateException("Transaction is autoCommit");
		if (!isEmpty()) log.info("commit()", toString());
		if (ensureIntegrityOnCommit) ensureIntegrityUntilUnchanged();
		backend.update(modified.getAll(), deleted, modifiedPropertiesByEntityId);
		backend.onTransactionFinished(this);
		modified = null;
		deleted = null;

		if (runnablesAfterCommit != null) {
			for (Runnable runnable : runnablesAfterCommit) {
				runnable.run();
			}
		}
	}

	private void ensureIntegrityUntilUnchanged() {
		String changeHash = createChangeHash();
		ensureIntegrity();
		if (changeHash.equals(createChangeHash())) return;
		ensureIntegrityUntilUnchanged();
	}

	private void ensureIntegrity() {
		ensuringIntegrity = true;
		try {
			for (AEntity entity : new ArrayList<AEntity>(modified.getAll())) {
				entity.ensureIntegrity();
			}
		} catch (EntityDeletedWhileEnsureIntegrity ex) {
			ensureIntegrity();
		} finally {
			ensuringIntegrity = false;
		}
	}

	private String createChangeHash() {
		StringBuilder sb = new StringBuilder();
		for (AEntity entity : modified.getAll()) {
			sb.append("/").append(entity.getId());
		}
		for (String id : deleted) {
			sb.append("/").append(id);
		}
		return sb.toString();
	}

	public void rollback() {
		log.info("rollback()", toString());
		backend.onTransactionFinished(this);
		modified = null;
		deleted = null;
	}

	public void persist(AEntity entity) {
		log.info("persist", toString(entity));
		if (autoCommit) {
			backend.update(Arrays.asList(entity), null, updatePropertiesMap(null, entity));
			return;
		}
		if (deleted.contains(entity))
			throw new IllegalStateException("Entity already deleted: " + Persistence.getTypeAndId(entity));
		modified.add(entity);
		updatePropertiesMap(modifiedPropertiesByEntityId, entity);
	}

	public void modified(AEntity entity, String field, Object value) {
		if (ignoreModifications) return;
		if (!contains(entity.getId())) return;
		log.info("modified", toString(entity), field, value);
		if (autoCommit) {
			backend.update(Arrays.asList(entity), null, updatePropertiesMap(null, entity, field, value));
			return;
		}
		modified.add(entity);
		updatePropertiesMap(modifiedPropertiesByEntityId, entity, field, value);
	}

	private String toString(AEntity entity) {
		if (entity == null) return "<null>";
		return Str.getSimpleName(entity.getClass()) + ":" + entity.getId();
	}

	public void delete(String entityId) {
		if (autoCommit) {
			backend.update(null, Arrays.asList(entityId), null);
			return;
		}
		deleted.add(entityId);
		modified.remove(entityId);
		if (ensuringIntegrity) throw new EntityDeletedWhileEnsureIntegrity();
	}

	public boolean isDeleted(String id) {
		return deleted.contains(id);
	}

	public boolean isDeleted(AEntity entity) {
		return isDeleted(entity.getId());
	}

	public boolean contains(String id) {
		if (deleted.contains(id)) return false;
		return modified.contains(id) || backend.contains(id);
	}

	public AEntity get(String id) {
		if (id == null) return null;
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

	public void setIgnoreModifications(boolean disabled) {
		this.ignoreModifications = disabled;
	}

	public Transaction setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
		return this;
	}

	@Override
	public synchronized String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		boolean empty = true;
		if (!modified.isEmpty()) {
			sb.append("\n    Modified: ").append(formatIds(modified.getAllIds()));
			empty = false;
		}
		if (!deleted.isEmpty()) {
			sb.append("\n    Deleted: ").append(formatIds(deleted));
			empty = false;
		}
		if (empty) sb.append(" Empty");
		return sb.toString();
	}

	private String formatIds(Collection<String> ids) {
		if (ids.isEmpty()) return "0";
		int size = ids.size();
		if (size <= 7) return Str.format(ids);
		return String.valueOf(size);
	}

	public void runAfterCommit(Runnable runnable) {
		if (autoCommit) {
			runnable.run();
			return;
		}
		if (runnablesAfterCommit == null) runnablesAfterCommit = new LinkedList<Runnable>();
		runnablesAfterCommit.add(runnable);
	}

	public boolean isEmpty() {
		return modified.isEmpty() && deleted.isEmpty();
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
