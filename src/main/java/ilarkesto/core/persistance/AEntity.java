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
import ilarkesto.core.base.Uuid;
import ilarkesto.core.time.DateAndTime;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AEntity implements Serializable, TransferableEntity {

	public static transient AEntityResolver entityResolver;

	private String id;
	private DateAndTime lastModified;

	public final void persist() {
		ensureIntegrity();
		entityResolver.persist(this);
	}

	/**
	 * Method gets called bevore persiting and after loading
	 */
	public void ensureIntegrity() {}

	/**
	 * Gets called when the master entity is deleted.
	 */
	protected void repairMissingMaster() {
		throw new IllegalStateException("Master entity is missing");
	}

	public final void updateLastModified() {
		lastModified = DateAndTime.now();
	}

	protected final void fireModified(String comment) {

	}

	protected boolean matches(SearchText search) {
		return search.matches(toString());
	}

	@Override
	public final DateAndTime getLastModified() {
		return lastModified;
	}

	@Override
	public final String getId() {
		if (id == null) id = Uuid.create();
		return id;
	}

	public void storeProperties(Map properties) {
		throw new RuntimeException(getClass().getName() + ".storeProperties(Map) not implemented!");
	}

	@Override
	public final Map createPropertiesMap() {
		Map properties = new HashMap();
		storeProperties(properties);
		return properties;
	}

	@Override
	public String toString() {
		return Utl.getSimpleName(getClass()) + ":" + getId();
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

	public static AEntity getById(String id) {
		return entityResolver.get(id);
	}

	protected static Set<String> getIdsAsSet(Collection<? extends AEntity> entities) {
		Set<String> result = new HashSet<String>(entities.size());
		for (AEntity entity : entities)
			result.add(entity.getId());
		return result;
	}

}
