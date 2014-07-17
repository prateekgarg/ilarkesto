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
package ilarkesto.persistence;

import ilarkesto.core.fp.Predicate;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class DummyEntityStore implements EntityStore {

	@Override
	public void deleteOldBackups() {}

	@Override
	public AEntity getById(String id) {
		return null;
	}

	@Override
	public List<AEntity> getByIds(Collection<String> ids) {
		return Collections.emptyList();
	}

	@Override
	public void setVersion(long version) {}

	@Override
	public void setAlias(String alias, Class cls) {}

	@Override
	public void load(Class<? extends AEntity> cls, String alias, boolean deleteOnFailure) {}

	@Override
	public AEntity getEntity(Predicate<Class> typeFilter, Predicate<AEntity> entityFilter) {
		return null;
	}

	@Override
	public int getEntitiesCount(Predicate<Class> typeFilter, Predicate<AEntity> entityFilter) {
		return 0;
	}

	@Override
	public Set<AEntity> getEntities(Predicate<Class> typeFilter, Predicate<AEntity> entityFilter) {
		return Collections.emptySet();
	}

	@Override
	public void persist(Collection<AEntity> entitiesToSave, Collection<AEntity> entitiesToDelete) {}

	@Override
	public void lock() {}

}
