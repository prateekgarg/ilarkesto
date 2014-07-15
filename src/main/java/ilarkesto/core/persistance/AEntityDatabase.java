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

import ilarkesto.core.base.RuntimeTracker;
import ilarkesto.core.logging.Log;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AEntityDatabase {

	protected final Log log = Log.get(getClass());

	public static AEntityDatabase instance;

	public abstract Transaction getTransaction();

	public abstract void onTransactionFinished(Transaction transaction);

	public abstract AEntity get(String id);

	public abstract List<AEntity> list(Collection<String> ids);

	public abstract AEntity get(AEntityQuery query);

	public abstract List<AEntity> list(AEntityQuery query);

	public abstract void update(Collection<AEntity> modified, Collection<String> deletedIds,
			Map<String, Map<String, String>> modifiedPropertiesByEntityId, Runnable callback);

	protected abstract Collection<AEntity> listAll();

	public boolean contains(String id) {
		try {
			return get(id) != null;
		} catch (EntityDoesNotExistException ex) {
			return false;
		}
	}

	public void ensureIntegrityForAllEntities() {
		log.info("Ensuring integrity for all entities");
		RuntimeTracker rt = new RuntimeTracker();
		Transaction transaction = getTransaction();
		ensureIntegrity();
		log.info("Integrity for all entities ensured in", rt.getRuntimeFormated());
		transaction.commit();
	}

	private void ensureIntegrity() {
		Collection<AEntity> entities = listAll();
		for (AEntity entity : entities) {
			try {
				entity.ensureIntegrity();
			} catch (Exception ex) {
				throw new RuntimeException("Ensuring integrity failed for entity: "
						+ Persistence.toStringWithTypeAndId(entity), ex);
			}
		}
	}

	public static AEntityDatabase get() {
		if (instance == null) throw new IllegalStateException("ADatabase.instance == null");
		return instance;
	}

}
