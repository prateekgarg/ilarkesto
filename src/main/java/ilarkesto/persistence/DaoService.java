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

import ilarkesto.core.logging.Log;
import ilarkesto.core.persistance.EntityDoesNotExistException;
import ilarkesto.core.time.Date;
import ilarkesto.core.time.DateAndTime;
import ilarkesto.core.time.Time;
import ilarkesto.di.Context;
import ilarkesto.id.IdentifiableResolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DaoService implements IdentifiableResolver<AEntity> {

	private static final Log LOG = Log.get(DaoService.class);

	private Map<Class, ADao> daos = new HashMap<Class, ADao>();

	public void ensureIntegrity() {
		if (!initialized) throw new RuntimeException("Not initiialized!");
		List<ADao> daosList = new ArrayList<ADao>(daos.values());
		Collections.sort(daosList);
		for (ADao dao : daosList) {
			dao.ensureIntegrity();
		}
	}

	public ADao getDaoByName(String entityName) {
		for (ADao manager : daos.values()) {
			if (manager.getEntityName().equals(entityName)) return manager;
		}
		throw new RuntimeException("Dao does not exist: entityName=" + entityName);
	}

	public ADao getDao(AEntity entity) {
		return getDaoByClass(entity.getClass());
	}

	public ADao getDaoByClass(Class entityClass) {
		ADao dao = daos.get(entityClass);
		if (dao == null) throw new RuntimeException("Dao does not exist: " + entityClass);
		return dao;
	}

	@Override
	public AEntity getById(String id) {
		return getEntityById(id);
	}

	public AEntity getEntityById(final String id) {
		if (id == null) throw new IllegalArgumentException("id == null");

		AEntity entity = Transaction.get().getById(id);

		if (entity == null) throw new EntityDoesNotExistException(id);
		return entity;
	}

	public boolean containsEntityWithId(final String id) {
		if (id == null) throw new IllegalArgumentException("id == null");
		return Transaction.get().containsWithId(id);
	}

	@Override
	public List<AEntity> getByIdsAsList(Collection<String> ids) {
		return getEntitiesByIds(ids);
	}

	public Set<AEntity> getByIdsAsSet(Collection<String> ids) {
		return new HashSet<AEntity>(getByIdsAsList(ids));
	}

	public List<AEntity> getEntitiesByIds(final Collection<String> ids) {
		List<AEntity> ret = new ArrayList<AEntity>(ids.size());
		for (String id : ids)
			ret.add(Transaction.get().getById(id));
		return ret;
	}

	// --- listeners ---

	private List<DaoListener> listeners;

	public void addListener(DaoListener listener) {
		if (listeners == null) listeners = new ArrayList<DaoListener>();
		listeners.add(listener);
	}

	public void removeListener(DaoListener listener) {
		if (listeners == null) return;
		listeners.remove(listener);
	}

	public void fireEntitySaved(AEntity entity) {
		if (listeners == null) return;
		EntityEvent event = new EntityEvent(this, entity);
		for (DaoListener listener : listeners)
			listener.entitySaved(event);
	}

	public void fireEntityDeleted(AEntity entity) {
		if (listeners == null) return;
		EntityEvent event = new EntityEvent(this, entity);
		for (DaoListener listener : listeners)
			listener.entityDeleted(event);
	}

	// --- dependencies ---

	private volatile boolean initialized;

	public synchronized final void initialize(Context context) {
		if (initialized) throw new RuntimeException("Already initialized!");

		for (ADao dao : context.getBeansByType(ADao.class)) {
			if (dao.getEntityClass() == null) continue;
			Map<String, Class> aliases = dao.getAliases();
			for (Map.Entry<String, Class> entry : aliases.entrySet()) {
				entityStore.setAlias(entry.getKey(), entry.getValue());

				// TODO clean up this mess
				String subpackageAndClass = entry.getValue().getName().substring(12);
				entityStore.setAlias("org.organizanto.app.domain." + subpackageAndClass, entry.getValue());
			}
		}
		entityStore.setAlias("ilarkesto.base.time.Date", Date.class);
		entityStore.setAlias("ilarkesto.base.time.Time", Time.class);
		entityStore.setAlias("ilarkesto.base.time.DateAndTime", DateAndTime.class);

		for (ADao dao : context.getBeansByType(ADao.class)) {
			if (dao.getEntityClass() == null) continue;
			dao.initialize(context);
		}

		for (ADao dao : context.getBeansByType(ADao.class)) {
			if (dao.getEntityClass() == null) continue;
			daos.put(dao.getEntityClass(), dao);
			entityStore.load(dao.getEntityClass(), dao.getEntityName(), dao.isSkipLoadingEntityOnFailure());
		}

		initialized = true;
	}

	private EntityStore entityStore;

	public void setEntityStore(EntityStore entityStore) {
		this.entityStore = entityStore;
	}

}
