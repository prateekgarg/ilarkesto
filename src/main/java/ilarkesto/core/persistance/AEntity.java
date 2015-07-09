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
import ilarkesto.core.base.Utl;
import ilarkesto.core.base.Uuid;
import ilarkesto.core.logging.Log;
import ilarkesto.core.search.SearchText;
import ilarkesto.core.time.DateAndTime;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AEntity implements TransferableEntity {

	private static final transient Log log = Log.get(AEntity.class);

	private String id;
	private Long modificationTime;

	@Deprecated
	private DateAndTime lastModified;

	protected final ValuesCache getCache() {
		return AEntityDatabase.get().getValuesCache(getId());
	}

	public final void persist() {
		updateLastModified();
		AEntityDatabase.get().getTransaction().persist(this);
		onAfterPersist();
	}

	protected void onAfterPersist() {}

	public final boolean isPersisted() {
		return AEntityDatabase.get().getTransaction().contains(getId());
	}

	public final void delete() {
		Transaction transaction = AEntityDatabase.get().getTransaction();
		if (transaction.isDeleted(this)) return;
		transaction.delete(getId());
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

	public final boolean isDeleted() {
		return AEntityDatabase.get().getTransaction().isDeleted(this);
	}

	private transient boolean ensuringIntegrity;

	/**
	 * Method gets called bevore persiting and after loading
	 */
	public final void ensureIntegrity() {
		if (ensuringIntegrity) return;
		if (isDeleted()) return;
		ensuringIntegrity = true;

		if (modificationTime == null && lastModified != null) {
			modificationTime = lastModified.toMillis();
			lastModified = null;
			fireModified("modificationTime", modificationTime.toString());
		}

		try {
			onEnsureIntegrity();
		} finally {
			ensuringIntegrity = false;
		}
	}

	protected void onEnsureIntegrity() {}

	@Override
	public void collectPassengers(TransferBus ret) {}

	/**
	 * Gets called when the master entity is deleted.
	 */
	protected void repairMissingMaster() {
		log.info("Deleting entity as repair for missing master:", Persistence.getTypeAndId(this));
		delete();
	}

	public final void updateLastModified() {
		modificationTime = System.currentTimeMillis();
	}

	protected final void fireModified(String field, String value) {
		Transaction.get().modified(this, field, value);
	}

	public boolean matches(SearchText search) {
		return search.matches(toString());
	}

	@Override
	public Long getModificationTime() {
		return modificationTime;
	}

	@Override
	public final String getId() {
		if (id == null) id = Uuid.create();
		return id;
	}

	public final AEntity setId(String id) {
		if (this.id != null) throw new IllegalStateException("id already set: " + this.id);
		this.id = id;
		return this;
	}

	public final boolean isId(String id) {
		return getId().equals(id);
	}

	@Override
	public final HashMap<String, String> createPropertiesMap() {
		HashMap<String, String> properties = new HashMap<String, String>();
		storeProperties(properties);
		return properties;
	}

	protected void storeProperties(Map<String, String> properties) {
		properties.put("@type", Str.getSimpleName(getClass()));
		properties.put("id", getId());
		properties.put("modificationTime", getModificationTime().toString());
	}

	public void updateProperties(Map<String, String> properties) {
		String idFromProperties = properties.get("id");
		if (!isId(idFromProperties))
			throw new IllegalArgumentException("Updating properties on " + Str.getSimpleName(getClass()) + " "
					+ getId() + " failed. Given properties have other id: " + idFromProperties);
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

	@Override
	public final int hashCode() {
		return getId().hashCode();
	}

	@Override
	public final boolean equals(Object obj) {
		if (!(obj instanceof AEntity)) return false;
		return getId().equals(((AEntity) obj).getId());
	}

	public static boolean exists(String id) {
		return Transaction.get().contains(id);
	}

	public static AEntity getById(String id) {
		if (id == null) return null;
		return Transaction.get().get(id);
	}

	public static List<AEntity> getByIds(Collection<String> ids) {
		return Transaction.get().list(ids);
	}

	public static Set<AEntity> getByIdsAsSet(Collection<String> ids) {
		return Transaction.get().listAsSet(ids);
	}

}
