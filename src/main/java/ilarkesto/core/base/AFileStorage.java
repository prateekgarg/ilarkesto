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
package ilarkesto.core.base;

import java.io.File;

public abstract class AFileStorage {

	private String path;

	public AFileStorage(String path) {
		super();
		this.path = path;
	}

	protected abstract File getBaseDir();

	public final File getFile(String relativePath) {
		File baseDir = getBaseDir();
		if (baseDir == null) return null;
		String sub = "";
		if (!Str.isBlank(path)) sub += "/" + path;
		if (!Str.isBlank(relativePath)) sub += "/" + relativePath;
		return new File(baseDir.getPath() + sub);
	}

	public final boolean isAvailable() {
		return getFile(null) != null;
	}

	public final AFileStorage getSubStorage(String path) {
		return new SubStorage(path);
	}

	@Override
	public String toString() {
		File file = getFile(null);
		if (file == null) return "<no base dir defined>";
		return file.getAbsolutePath();
	}

	private class SubStorage extends AFileStorage {

		private String sub;

		public SubStorage(String sub) {
			super(null);
			this.sub = sub;
		}

		@Override
		protected File getBaseDir() {
			return AFileStorage.this.getFile(sub);
		}

	}

}
