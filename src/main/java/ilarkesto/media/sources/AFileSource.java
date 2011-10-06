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
package ilarkesto.media.sources;

import ilarkesto.async.ACallback;
import ilarkesto.async.Callback;
import ilarkesto.async.fs.ListFiles;
import ilarkesto.media.AMediaItem;
import ilarkesto.media.MediaItemFilter;
import ilarkesto.media.MediaSource;

import java.io.File;
import java.util.List;

public abstract class AFileSource implements MediaSource {

	private File rootDir;

	public AFileSource(File rootDir) {
		super();
		this.rootDir = rootDir;
	}

	protected abstract void onFilesFound(List<File> files, Callback<List<AMediaItem>> callback);

	@Override
	public final void listItems(MediaItemFilter filter, final Callback<List<AMediaItem>> callback) {
		new ListFiles(rootDir).setIncludeDirs(true).setRecurse(true).start(new ACallback<List<File>>(callback) {

			@Override
			public void onSuccess(List<File> result) {
				onFilesFound(result, callback);
			}
		});
	}
}
