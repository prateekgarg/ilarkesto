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

import ilarkesto.core.base.Args;
import ilarkesto.core.base.RuntimeTracker;
import ilarkesto.core.base.Str;
import ilarkesto.core.logging.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ATransaction<E extends Entity> implements EntitiesProvider<E> {

	protected final Log log = Log.get(getClass());

	private String name;
	private boolean autoCommit;
	private boolean ignoreModifications;
	private boolean ensureIntegrityOnCommit;
	private boolean ensuringIntegrity;
	private LinkedList<Runnable> runnablesAfterCommit;
	private int transactionNumberCounter = 0;

	private EntitiesCache<E> modified = new EntitiesCache<E>();
	private Map<String, Map<String, String>> modifiedPropertiesByEntityId = new HashMap<String, Map<String, String>>();
	private Set<String> deleted = new HashSet<String>();

	public ATransaction(String name, boolean autoCommit, boolean ensureIntegrityOnCommit) {
		super();
		this.name = "#" + (++transactionNumberCounter) + " (" + name + ")";
		this.autoCommit = autoCommit;
		this.ensureIntegrityOnCommit = ensureIntegrityOnCommit;
	}

	public void commit() {
		// if (autoCommit) throw new IllegalStateException("Transaction is autoCommit");
		if (!isEmpty()) {
			log.info("commit()", toString());
			if (ensureIntegrityOnCommit) ensureIntegrityUntilUnchanged();
			getBackend().update(modified.getAll(), deleted, modifiedPropertiesByEntityId, new CommitCallback());
		} else {
			new CommitCallback().run();
		}
		getBackend().onTransactionFinished(this);
		modified = null;
		deleted = null;
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
			for (E entity : new ArrayList<E>(modified.getAll())) {
				entity.ensureIntegrity();
			}
			for (String id : deleted) {
				E deletedEntity;
				try {
					deletedEntity = getBackend().getById(id);
				} catch (EntityDoesNotExistException ex) {
					continue;
				}

				Set<Entity> referencedEntities = deletedEntity.getReferencedEntities();
				log.debug("Ensuring integrity for referenced entities of deleted entity:",
					Persistence.toStringWithTypeAndId(deletedEntity), referencedEntities);

				for (Entity referencedEntity : referencedEntities) {
					if (referencedEntity == null) continue;
					referencedEntity.ensureIntegrity();
				}
			}
		} catch (EntityDeletedWhileEnsureIntegrity ex) {
			// redo (changeHash changed)
		} finally {
			ensuringIntegrity = false;
		}
	}

	private String createChangeHash() {
		StringBuilder sb = new StringBuilder();
		for (E entity : modified.getAll()) {
			sb.append("/").append(entity.getId());
		}
		for (String id : deleted) {
			sb.append("/").append(id);
		}
		return sb.toString();
	}

	public void rollback() {
		log.info("rollback()", toString());
		getBackend().onTransactionFinished(this);
		modified = null;
		deleted = null;
	}

	public void persist(E entity) {
		log.info("PERSIST", toString(entity));
		if (autoCommit) {
			getBackend().update(Arrays.asList(entity), null, updatePropertiesMap(null, entity), new CommitCallback());
			return;
		}
		if (deleted.contains(entity))
			throw new IllegalStateException("Entity already deleted: " + Persistence.getTypeAndId(entity));
		modified.add(entity);
		updatePropertiesMap(modifiedPropertiesByEntityId, entity);
	}

	public void modified(E entity, String field, String value) {
		if (ignoreModifications) return;
		if (!containsWithId(entity.getId())) return;
		log.info(name, "MODIFIED", toString(entity), field, value);
		if (autoCommit) {
			getBackend().update(Arrays.asList(entity), null, updatePropertiesMap(null, entity, field, value),
				new CommitCallback());
			return;
		}
		modified.add(entity);
		getBackend().onEntityModified();
		updatePropertiesMap(modifiedPropertiesByEntityId, entity, field, value);
	}

	private String toString(E entity) {
		if (entity == null) return "<null>";
		return Str.getSimpleName(entity.getClass()) + ":" + entity.getId();
	}

	public void delete(String entityId) {
		log.info("DELETE", entityId);
		if (autoCommit) {
			getBackend().update(null, Arrays.asList(entityId), null, new CommitCallback());
			return;
		}
		if (deleted.contains(entityId)) {
			log.debug("Already deleted:", entityId);
			return;
		}
		deleted.add(entityId);
		modified.remove(entityId);
		getBackend().onEntityModified();
		if (ensuringIntegrity) throw new EntityDeletedWhileEnsureIntegrity();
	}

	@Override
	public boolean containsWithId(String id) {
		if (deleted.contains(id)) return false;
		return modified.containsWithId(id) || getBackend().containsWithId(id);
	}

	@Override
	public E getById(String id) {
		Args.assertNotNull(id, "id");
		if (deleted.contains(id)) throw new EntityDoesNotExistException(id);
		if (modified.containsWithId(id)) return modified.getById(id);
		return getBackend().getById(id);
	}

	@Override
	public E findFirst(AEntityQuery query) {
		E entity = modified.findFirst(query);
		if (entity == null) entity = getBackend().findFirst(query);
		if (entity != null && deleted.contains(entity.getId())) return null;
		return entity;
	}

	@Override
	public <C extends Collection<E>> C findAll(AEntityQuery<E> query, C resultCollection) {
		RuntimeTracker rt = new RuntimeTracker();

		getBackend().findAll(query, resultCollection);
		modified.findAll(query, resultCollection);

		Iterator<E> iterator = resultCollection.iterator();
		while (iterator.hasNext()) {
			E entity = iterator.next();
			if (deleted.contains(entity.getId())) iterator.remove();
		}

		long time = rt.getRuntime();
		if (time > 100) {
			log.log(time > 1000 ? Log.Level.WARN : Log.Level.DEBUG, "Query provided", resultCollection.size(),
				"elements in", rt.getRuntimeFormated(), query);
		}
		return resultCollection;
	}

	public void setIgnoreModifications(boolean disabled) {
		this.ignoreModifications = disabled;
	}

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	public boolean isAutoCommit() {
		return autoCommit;
	}

	@Override
	public String toString() {
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
		return (modified == null || modified.isEmpty()) && (deleted == null || deleted.isEmpty());
	}

	public String getName() {
		return name;
	}

	private static Map<String, Map<String, String>> updatePropertiesMap(
			Map<String, Map<String, String>> modifiedPropertiesByEntityId, Entity entity, String field, String value) {
		if (modifiedPropertiesByEntityId == null)
			modifiedPropertiesByEntityId = new HashMap<String, Map<String, String>>();
		String id = entity.getId();
		Map<String, String> properties = modifiedPropertiesByEntityId.get(id);
		if (properties == null) {
			properties = new HashMap<String, String>();
			properties.put("id", id);
			modifiedPropertiesByEntityId.put(id, properties);
		}
		properties.put(field, value);
		return modifiedPropertiesByEntityId;
	}

	private static Map<String, Map<String, String>> updatePropertiesMap(
			Map<String, Map<String, String>> modifiedPropertiesByEntityId, Entity entity) {
		if (modifiedPropertiesByEntityId == null)
			modifiedPropertiesByEntityId = new HashMap<String, Map<String, String>>();
		String id = entity.getId();
		Map<String, String> properties = entity.createPropertiesMap();
		modifiedPropertiesByEntityId.put(id, properties);
		return modifiedPropertiesByEntityId;
	}

	class CommitCallback implements Runnable {

		@Override
		public void run() {
			if (runnablesAfterCommit != null) {
				for (Runnable runnable : runnablesAfterCommit) {
					runnable.run();
				}
			}
		}

	}

	protected abstract EntitiesBackend<E, ATransaction<E>> getBackend();

	@Override
	public final List<E> getByIdsAsList(Collection<String> ids) throws EntityDoesNotExistException {
		return getByIds(ids, new ArrayList<E>(ids.size()));
	}

	@Override
	public final Set<E> getByIdsAsSet(Collection<String> ids) throws EntityDoesNotExistException {
		return getByIds(ids, new HashSet<E>(ids.size()));
	}

	@Override
	public final <C extends Collection<E>> C getByIds(Collection<String> ids, C resultContainer)
			throws EntityDoesNotExistException {
		for (String id : ids) {
			resultContainer.add(getById(id));
		}
		return resultContainer;
	}

	public final Set<E> findAllAsSet(AEntityQuery query) {
		return findAll(query, new HashSet<E>());
	}

	public final List<E> findAllAsList(AEntityQuery query) {
		return findAll(query, new ArrayList<E>());
	}

}
