/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>, Artjom Kochtchi
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.persistence;

import ilarkesto.base.Utl;
import ilarkesto.core.logging.Log;
import ilarkesto.fp.Predicate;
import ilarkesto.id.IdentifiableResolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Transaction implements IdentifiableResolver<AEntity> {

	private static final Log log = Log.get(Transaction.class);

	private static int count = 0;
	private int no;

	private EntityStore entityStore;

	private String threadName;
	private List<AEntity> entitiesToSave = new ArrayList<AEntity>();
	private List<AEntity> entitiesToDelete = new ArrayList<AEntity>();
	private List<AEntity> entitiesRegistered = new ArrayList<AEntity>();

	public Transaction(EntityStore entityStore) {
		synchronized (getClass()) {
			no = ++count;
		}
		this.entityStore = entityStore;
		threadName = Thread.currentThread().getName();
	}

	/**
	 * avoids infinite loops within <code>ensureIntegrity()</code>
	 */
	private AEntity currentlySaving;

	synchronized void saveEntity(AEntity entity) {
		if (entity == null) throw new NullPointerException("entity");
		if (currentlySaving == entity) return;
		currentlySaving = entity;
		if (entitiesToSave.contains(entity) || entitiesToDelete.contains(entity)) return;
		log.debug("SAVE", toStringWithType(entity), "@", this);
		entitiesToSave.add(entity);
		currentlySaving = null;
	}

	synchronized void deleteEntity(AEntity entity) {
		if (entitiesToDelete.contains(entity)) return;
		log.debug("DELETE", toStringWithType(entity), "@", this);
		entitiesToDelete.add(entity);
		entitiesToSave.remove(entity);
	}

	synchronized void registerEntity(AEntity entity) {
		entitiesRegistered.add(entity);
	}

	boolean committed;

	synchronized void commit() {
		if (committed) throw new RuntimeException("Transaction already committed: " + this);
		committed = true;

		if (isEmpty()) {
			log.debug("Committing empty transaction:", this);
		} else {
			log.info("Committing transaction:", this);
		}

		Collection<AEntity> savedEntities = new HashSet<AEntity>(entitiesToSave.size());

		while (!isEmpty()) {
			for (AEntity entity : new ArrayList<AEntity>(entitiesToSave)) {
				entityStore.save(entity);
				entitiesToSave.remove(entity);
				savedEntities.add(entity);
			}

			for (AEntity entity : new ArrayList<AEntity>(entitiesToDelete)) {
				entityStore.delete(entity);
				entitiesToDelete.remove(entity);
				savedEntities.remove(entity);
			}

			for (AEntity entity : savedEntities) {
				entity.ensureIntegrity();
			}
		}

		entitiesRegistered.clear();

		log.debug("Transaction committed:", this);
	}

	synchronized boolean isPersistent(String id) {
		AEntity result = entityStore.getById(id);
		if (result != null) return true;

		for (AEntity entity : entitiesToSave) {
			if (id.equals(entity.getId())) return true;
		}

		// ignore registeredEntities!
		return false;
	}

	synchronized boolean isEmpty() {
		return entitiesToDelete.isEmpty() && entitiesToSave.isEmpty();
	}

	@Override
	public synchronized List<AEntity> getByIds(Collection<String> ids) {
		List<AEntity> result = entityStore.getByIds(ids);
		for (AEntity entity : entitiesToSave) {
			if (ids.contains(entity.getId())) {
				result.remove(entity);
				result.add(entity);
			}
		}
		for (AEntity entity : entitiesRegistered) {
			if (ids.contains(entity.getId())) {
				result.remove(entity);
				result.add(entity);
			}
		}
		result.removeAll(entitiesToDelete);
		return result;
	}

	synchronized Set<AEntity> getEntities(Predicate<Class> typeFilter, Predicate<AEntity> entityFilter) {
		Set<AEntity> result = entityStore.getEntities(typeFilter, entityFilter);
		for (AEntity entity : entitiesToSave) {
			if (Persist.test(entity, typeFilter, entityFilter)) result.add(entity);
		}
		for (AEntity entity : entitiesRegistered) {
			if (Persist.test(entity, typeFilter, entityFilter)) result.add(entity);
		}
		result.removeAll(entitiesToDelete);
		return result;
	}

	int getEntitiesCount(Predicate<Class> typeFilter, Predicate<AEntity> entityFilter) {
		return entityStore.getEntitiesCount(typeFilter, entityFilter);
	}

	@Override
	public synchronized AEntity getById(String id) {
		AEntity result = entityStore.getById(id);
		if (result == null) {
			for (AEntity entity : entitiesToSave) {
				if (id.equals(entity.getId())) return entity;
			}
			for (AEntity entity : entitiesRegistered) {
				if (id.equals(entity.getId())) return entity;
			}
		} else {
			if (entitiesToDelete.contains(result)) return null;
		}
		return result;
	}

	synchronized AEntity getEntity(Predicate<Class> typeFilter, Predicate<AEntity> entityFilter) {
		AEntity result = entityStore.getEntity(typeFilter, entityFilter);
		if (result == null) {
			for (AEntity entity : entitiesToSave) {
				if (Persist.test(entity, typeFilter, entityFilter) && !entitiesToDelete.contains(entity))
					return entity;
			}
			for (AEntity entity : entitiesRegistered) {
				if (Persist.test(entity, typeFilter, entityFilter) && !entitiesToDelete.contains(entity))
					return entity;
			}
		} else {
			if (entitiesToDelete.contains(result)) return null;
		}
		return result;
	}

	@Override
	public synchronized String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("#").append(no);
		sb.append(" (").append(threadName).append(")");
		if (!entitiesToSave.isEmpty()) {
			sb.append("\n    SAVE: ").append(toString(entitiesToSave));
		}
		if (!entitiesRegistered.isEmpty()) {
			sb.append("\n    REGISTERED: ").append(toString(entitiesRegistered));
		}
		if (!entitiesToDelete.isEmpty()) {
			sb.append("\n    DELETE: ").append(toString(entitiesToDelete));
		}
		return sb.toString();
	}

	private String toString(Collection<AEntity> entities) {
		StringBuilder sb = new StringBuilder();
		for (AEntity entity : entities) {
			if (entity == null) {
				sb.append("\n        null");
			} else {
				sb.append("\n        ").append(entity.getClass().getSimpleName()).append(": ")
						.append(entity.toString());
			}
		}
		return sb.toString();
	}

	private String toStringWithType(Object o) {
		try {
			return Utl.toStringWithType(o);
		} catch (Exception ex) {
			return o.getClass().getSimpleName();
		}
	}

}
