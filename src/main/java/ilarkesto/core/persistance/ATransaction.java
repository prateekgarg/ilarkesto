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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ATransaction<E extends Entity> implements EntitiesProvider<E> {

	public abstract void commit();

	public abstract void rollback();

	@Override
	public final List<E> getByIdsAsList(Collection<String> ids) throws EntityDoesNotExistException {
		return getByIds(ids, new ArrayList<E>(ids.size()));
	}

	@Override
	public final Set<E> getByIdsAsSet(Collection<String> ids) throws EntityDoesNotExistException {
		return getByIds(ids, new HashSet<E>(ids.size()));
	}

	@Override
	public final <C extends Collection<E>> C getByIds(Collection<String> ids, C resultContainer)
			throws EntityDoesNotExistException {
		for (String id : ids) {
			resultContainer.add(getById(id));
		}
		return resultContainer;
	}

}
