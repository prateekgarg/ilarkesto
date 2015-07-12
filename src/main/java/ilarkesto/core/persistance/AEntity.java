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

import ilarkesto.core.base.Utl;
import ilarkesto.core.logging.Log;
import ilarkesto.core.search.SearchText;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AEntity extends ABaseEntity implements TransferableEntity {

	private static final transient Log log = Log.get(AEntity.class);

	@Override
	public AEntity setId(String id) {
		super.setId(id);
		return this;
	}

	protected final ValuesCache getCache() {
		return AEntityDatabase.get().getValuesCache(getId());
	}

	@Override
	protected void doPersist() {
		Transaction.get().persist(this);
	}

	public final boolean isPersisted() {
		return Transaction.get().containsWithId(getId());
	}

	@Override
	public final void delete() {
		Transaction.get().delete(getId());
	}

	/**
	 * Provides all referenced entities. Back-references included.
	 */
	public Set<AEntity> getReferencedEntities() {
		return new HashSet<AEntity>();
	}

	public String getDeleteVeto() {
		return "Objekt ist nicht löschbar";
	}

	public final boolean isDeletable() {
		return getDeleteVeto() == null;
	}

	private transient boolean ensuringIntegrity;

	/**
	 * Method gets called bevore persiting and after loading
	 */
	public final void ensureIntegrity() {
		if (ensuringIntegrity) return;
		if (!isPersisted()) return;
		ensuringIntegrity = true;

		try {
			onEnsureIntegrity();
		} finally {
			ensuringIntegrity = false;
		}
	}

	protected void onEnsureIntegrity() {}

	/**
	 * Gets called when the master entity is deleted.
	 */
	protected void repairMissingMaster() {
		log.info("Deleting entity as repair for missing master:", Persistence.getTypeAndId(this));
		delete();
	}

	protected final void fireModified(String field, String value) {
		Transaction.get().modified(this, field, value);
	}

	public boolean matches(SearchText search) {
		return search.matches(toString());
	}

	protected String asString() {
		return Utl.getSimpleName(getClass()) + ":" + getId();
	}

	@Override
	public final String toString() {
		try {
			return asString();
		} catch (Exception ex) {
			return "asString()-ERROR: " + Utl.getSimpleName(getClass()) + ":" + getId();
		}
	}

	public static boolean exists(String id) {
		return Transaction.get().containsWithId(id);
	}

	public static AEntity getById(String id) {
		if (id == null) return null;
		return Transaction.get().getById(id);
	}

	public static List<AEntity> getByIds(Collection<String> ids) {
		return Transaction.get().getByIdsAsList(ids);
	}

	public static Set<AEntity> getByIdsAsSet(Collection<String> ids) {
		return Transaction.get().getByIdsAsSet(ids);
	}

}
