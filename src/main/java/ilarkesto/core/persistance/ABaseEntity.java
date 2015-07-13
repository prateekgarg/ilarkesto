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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class ABaseEntity implements Entity, TransferableEntity {

	private String id;
	private Long modificationTime;
	private transient boolean ensuringIntegrity;

	protected abstract void doPersist();

	/**
	 * Method gets called bevore persiting and after loading
	 */
	@Override
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
	 * Provides all referenced entities. Back-references included.
	 */
	@Override
	public Set<Entity> getReferencedEntities() {
		return new HashSet<Entity>();
	}

	public void updateProperties(Map<String, String> properties) {
		String idFromProperties = properties.get("id");
		if (!isId(idFromProperties))
			throw new IllegalArgumentException("Updating properties on " + Str.getSimpleName(getClass()) + " "
					+ getId() + " failed. Given properties have other id: " + idFromProperties);
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

	@Override
	public void collectPassengers(TransferBus bus) {}

	@Override
	public final void persist() {
		updateLastModified();
		doPersist();
		onAfterPersist();
	}

	protected void onAfterPersist() {}

	@Override
	public final String getId() {
		if (id == null) id = Uuid.create();
		return id;
	}

	public Entity setId(String id) {
		if (this.id != null) throw new IllegalStateException("id already set: " + this.id);
		this.id = id;
		return this;
	}

	public final boolean isId(String id) {
		return getId().equals(id);
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
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (!getClass().equals(o.getClass())) return false;
		return isId(((Entity) o).getId());
	}

	public final void updateLastModified() {
		modificationTime = System.currentTimeMillis();
		onAfterUpdateLastModified();
	}

	protected void onAfterUpdateLastModified() {}

	@Override
	public Long getModificationTime() {
		return modificationTime;
	}

}
