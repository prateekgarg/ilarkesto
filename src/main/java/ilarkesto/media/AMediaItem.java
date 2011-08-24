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
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.media;

import java.util.List;

public abstract class AMediaItem<M extends AMetadata> {

	public abstract String getPlayUrl();

	public abstract M getMetadata();

	public abstract List<AMediaItem> getChildren();

	public boolean containsChildren() {
		List<AMediaItem> children = getChildren();
		return children != null && !children.isEmpty();
	}

	@Override
	public String toString() {
		return getMetadata().getFullTitle();
	}

}
