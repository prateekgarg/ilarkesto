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
import ilarkesto.core.fp.FP;
import ilarkesto.core.logging.Log;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AEntityDatabase implements EntitiesBackend<AEntity, Transaction> {

	protected final Log log = Log.get(getClass());

	public static AEntityDatabase instance;

	private Map<String, ValuesCache> valuesCachesById = createValuesCachesMap();

	protected abstract Map<String, ValuesCache> createValuesCachesMap();

	public abstract AEntity get(String id);

	public abstract Set<AEntity> list(Collection<String> ids);

	public abstract AEntity get(AEntityQuery query);

	public abstract Set<AEntity> list(AEntityQuery query);

	public abstract boolean isTransactionWithChangesOpen();

	public abstract Collection<AEntity> listAll();

	final ValuesCache getValuesCache(String id) {
		ValuesCache cache = valuesCachesById.get(id);
		if (cache == null) {
			cache = new ValuesCache();
			valuesCachesById.put(id, cache);
		}
		return cache;
	}

	final void onEntityModified() {
		clearCaches();
	}

	public void clearCaches() {
		valuesCachesById.clear();
	}

	public boolean isPartial() {
		return false;
	}

	public boolean contains(String id) {
		try {
			return get(id) != null;
		} catch (EntityDoesNotExistException ex) {
			return false;
		}
	}

	public void ensureIntegrityForAllEntities() {
		Collection<AEntity> entities = listAll();
		int count = entities.size();
		log.info("Ensuring integrity for all", count, "entities");
		RuntimeTracker rt = new RuntimeTracker();
		Transaction transaction = getTransaction();

		Map<Class<? extends AEntity>, List<AEntity>> entitiesByType = FP.group(entities,
			new EntitiesByTypeGroupFunctionn());
		for (Map.Entry<Class<? extends AEntity>, List<AEntity>> entry : entitiesByType.entrySet()) {
			Class<? extends AEntity> type = entry.getKey();
			List<AEntity> entitiesOfType = entry.getValue();
			log.info("   ", type.getSimpleName(), entitiesOfType.size());
			ensureIntegrity(entitiesOfType);
		}

		log.info("Integrity for all", count, "entities ensured in", rt.getRuntimeFormated());
		transaction.commit();
	}

	private void ensureIntegrity(Collection<AEntity> entities) {
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

	public String createInfo() {
		return getClass().getName();
	}

}
