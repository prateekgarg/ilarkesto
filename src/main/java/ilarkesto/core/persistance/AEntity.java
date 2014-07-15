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
import ilarkesto.core.time.DateAndTime;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AEntity implements Serializable, TransferableEntity {

	private static final transient Log log = Log.get(AEntity.class);

	private String id;
	private DateAndTime lastModified;

	public final void persist() {
		AEntityDatabase.get().getTransaction().persist(this);
	}

	public final boolean isPersisted() {
		return AEntityDatabase.get().getTransaction().contains(getId());
	}

	public final void delete() {
		AEntityDatabase.get().getTransaction().delete(getId());
		if (Transaction.get().isAutoCommit()) return;
		ensureIntegrityForReferencesAfterDelete();
	}

	protected void ensureIntegrityForReferencesAfterDelete() {}

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
		try {
			onEnsureIntegrity();
		} finally {
			ensuringIntegrity = false;
		}
	}

	protected void onEnsureIntegrity() {}

	@Override
	public Set<AEntity> getPassengers() {
		return new HashSet<AEntity>();
	}

	/**
	 * Gets called when the master entity is deleted.
	 */
	protected void repairMissingMaster() {
		log.info("Deleting entity as repair for missing master:", Persistence.getTypeAndId(this));
		delete();
	}

	public final void updateLastModified() {
		lastModified = DateAndTime.now();
	}

	protected final void fireModified(String field, String value) {
		Transaction.get().modified(this, field, value);
	}

	public boolean matches(SearchText search) {
		return search.matches(toString());
	}

	@Override
	public final DateAndTime getLastModified() {
		if (lastModified == null) lastModified = DateAndTime.now();
		return lastModified;
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

	public void storeProperties(Map<String, String> properties) {
		properties.put("@type", Str.getSimpleName(getClass()));
		properties.put("id", getId());
	}

	public void updateProperties(Map<String, String> properties) {
		if (!isId(properties.get("id"))) throw new IllegalArgumentException("properties from other entity");
	}

	@Override
	public final Map<String, String> createPropertiesMap() {
		Map<String, String> properties = new HashMap<String, String>();
		storeProperties(properties);
		return properties;
	}

	protected String asString() {
		return Utl.getSimpleName(getClass()) + ":" + getId();
	}

	@Override
	public final String toString() {
		try {
			return asString();
		} catch (Exception ex) {
			Log.get(getClass()).error("asString() throwed exception:", ex);
			return Utl.getSimpleName(getClass()) + ":" + getId();
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
		return AEntityDatabase.get().getTransaction().contains(id);
	}

	public static AEntity getById(String id) {
		if (id == null) return null;
		return AEntityDatabase.get().getTransaction().get(id);
	}

}
