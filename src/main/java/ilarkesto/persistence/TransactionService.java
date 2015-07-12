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

import ilarkesto.core.fp.Predicate;
import ilarkesto.core.logging.Log;
import ilarkesto.core.scope.In;
import ilarkesto.id.IdentifiableResolver;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class TransactionService implements IdentifiableResolver<AEntity> {

	private static final Log log = Log.get(TransactionService.class);

	@In
	private EntityStore entityStore;

	public TransactionService() {}

	public boolean isPersistent(String id) {
		if (id == null) return false;
		Transaction transaction = Transaction.get();
		return transaction.isPersistent(id);
	}

	public boolean isDeleted(AEntity entity) {
		if (entity == null) return false;
		Transaction transaction = Transaction.get();
		return transaction.isDeleted(entity);
	}

	// --- delegations ---

	@Deprecated
	@Override
	public AEntity getById(String id) {
		Transaction transaction = Transaction.get();
		return transaction.getById(id);
	}

	@Deprecated
	public AEntity getEntity(Predicate<Class> typeFilter, Predicate<AEntity> entityFilter) {
		Transaction transaction = Transaction.get();
		return transaction.getEntity(typeFilter, entityFilter);
	}

	@Override
	public List<AEntity> getByIds(Collection<String> ids) {
		Transaction transaction = Transaction.get();
		if (transaction == null) {
			return entityStore.getByIds(ids);
		} else {
			return transaction.getByIds(ids);
		}
	}

	public Set<AEntity> getEntities(Predicate<Class> typeFilter, Predicate<AEntity> entityFilter) {
		Transaction transaction = Transaction.get();
		Set<AEntity> ret;
		if (transaction == null) {
			ret = entityStore.getEntities(typeFilter, entityFilter);
		} else {
			ret = transaction.getEntities(typeFilter, entityFilter);
		}
		return ret;
	}

	public int getEntitiesCount(Predicate<Class> typeFilter, Predicate<AEntity> entityFilter) {
		Transaction transaction = Transaction.get();
		int ret;
		if (transaction == null) {
			ret = entityStore.getEntitiesCount(typeFilter, entityFilter);
		} else {
			ret = transaction.getEntitiesCount(typeFilter, entityFilter);
		}
		return ret;
	}

	public synchronized void deleteEntity(AEntity entity) {
		Transaction.get().deleteEntity(entity);
	}

	public synchronized void saveEntity(AEntity entity) {
		Transaction.get().saveEntity(entity);
	}

	public synchronized void registerEntity(AEntity entity) {
		Transaction.get().registerEntity(entity);
	}
}
