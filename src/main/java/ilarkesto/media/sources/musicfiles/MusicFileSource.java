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

import ilarkesto.async.Callback;
import ilarkesto.io.IO;
import ilarkesto.media.AMediaItem;
import ilarkesto.media.sources.AFileSource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicFileSource extends AFileSource {

	public MusicFileSource(File rootDir) {
		super(rootDir);
	}

	@Override
	protected void onFilesFound(List<File> files, Callback<List<AMediaItem>> callback) {
		List<AMediaItem> ret = new ArrayList<AMediaItem>();
		for (File file : files) {

		}
		callback.onSuccess(ret);
	}

	private boolean isMusicFile(File file) {
		String ext = IO.getFileExtension(file.getName());
		if (ext == null) return false;
		if (ext.equals("ogg")) return true;
		if (ext.equals("mp3")) return true;
		return false;
	}

}
