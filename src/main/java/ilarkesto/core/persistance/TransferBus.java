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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TransferBus {

	private Set<TransferableEntity> entities = new HashSet<TransferableEntity>();

	public void add(TransferableEntity entity) {
		if (entity == null) return;
		boolean added = entities.add(entity);
		if (added) {
			entity.collectPassengers(this);
		}
	}

	public void add(TransferableEntity... entities) {
		if (entities == null || entities.length == 0) return;
		for (TransferableEntity entity : entities) {
			if (entity == null) return;
			add(entity);
		}
	}

	public void add(Collection<? extends TransferableEntity> entities) {
		if (entities == null || entities.isEmpty()) return;
		for (TransferableEntity entity : entities) {
			if (entity == null) return;
			add(entity);
		}
	}

	public Set<TransferableEntity> getEntities() {
		return entities;
	}

}
