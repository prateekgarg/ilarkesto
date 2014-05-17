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

import ilarkesto.core.logging.Log;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Transaction {

	private static final Log log = Log.get(Transaction.class);

	private String name;
	private boolean autoCommit;
	private AEntityDatabase backend;

	private EntityCache modified = new EntityCache();
	private Set<String> deleted = new HashSet<String>();

	public Transaction(AEntityDatabase backend, String name, boolean autoCommit) {
		super();
		this.backend = backend;
		this.name = name;
		this.autoCommit = autoCommit;
	}

	public void commit() {
		log.info("commit()", toString());
		backend.update(modified.getAll(), deleted);
		modified = null;
		deleted = null;
		backend.onTransactionFinished(this);
	}

	public void rollback() {
		log.info("rollback()", toString());
		backend.onTransactionFinished(this);
	}

	public void persist(AEntity entity) {
		if (autoCommit) {
			backend.update(Arrays.asList(entity), null);
			return;
		}
		modified.add(entity);
	}

	public void modified(AEntity entity, String field, Object value) {
		if (autoCommit) {
			backend.update(Arrays.asList(entity), null);
			return;
		}
		modified.add(entity);
	}

	public void delete(String entityId) {
		if (autoCommit) {
			backend.update(null, Arrays.asList(entityId));
			return;
		}
		deleted.add(entityId);
		modified.remove(entityId);
	}

	public AEntity get(String id) {
		return backend.get(id);
	}

	public List<AEntity> list(Collection<String> ids) {
		return backend.list(ids);
	}

	public AEntity get(AEntityQuery query) {
		return backend.get(query);
	}

	public List<AEntity> list(AEntityQuery query) {
		return backend.list(query);
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

	public static Transaction get() {
		return AEntityDatabase.get().getTransaction();
	}

}
