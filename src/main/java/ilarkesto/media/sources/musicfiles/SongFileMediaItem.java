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
package ilarkesto.media.sources.musicfiles;

import ilarkesto.media.AFileMediaItem;
import ilarkesto.media.metadata.SongMetadata;

import java.io.File;

public class SongFileMediaItem extends AFileMediaItem<SongMetadata> {

	public SongFileMediaItem(File file) {
		super(file);
	}

	private SongMetadata metadata;

	@Override
	public SongMetadata getMetadata() {
		if (metadata == null) {
			metadata = new SongMetadata();
			metadata.setTitle(file.getName());
			// TODO
		}
		return metadata;
	}

	@Override
	public boolean containsChildren() {
		return false;
	}

}
