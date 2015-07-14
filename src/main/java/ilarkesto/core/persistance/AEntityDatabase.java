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

import ilarkesto.core.logging.Log;

import java.util.Set;

public abstract class AEntityDatabase implements EntitiesBackend<AEntity, Transaction> {

	protected final Log log = Log.get(getClass());

	@Override
	public abstract AEntity findFirst(AEntityQuery query);

	public abstract Set<AEntity> findAllAsSet(AEntityQuery query);

	public AEntityDatabase() {
		Transaction.backend = this;
	}

	public boolean isPartial() {
		return false;
	}

	@Override
	public boolean containsWithId(String id) {
		try {
			return getById(id) != null;
		} catch (EntityDoesNotExistException ex) {
			return false;
		}
	}

	@Override
	public String createInfo() {
		return getClass().getName();
	}

}
