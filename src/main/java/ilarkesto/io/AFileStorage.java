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
package ilarkesto.io;

import ilarkesto.core.base.Str;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public abstract class AFileStorage {

	private String path;

	public AFileStorage(String path) {
		super();
		this.path = path;
	}

	protected abstract File getBaseDir();

	public final Set<File> listFiles() {
		Set<File> ret = new HashSet<File>();
		File baseDir = getBaseDir();
		if (baseDir == null) return ret;
		String sub = "";
		if (!Str.isBlank(path)) sub += "/" + path;
		File dir = new File(baseDir.getPath() + sub);
		if (!dir.exists()) return ret;
		if (!dir.isDirectory()) return ret;
		for (File file : dir.listFiles()) {
			ret.add(file);
		}
		return ret;
	}

	public final File getFile(String relativePath) {
		File baseDir = getBaseDir();
		if (baseDir == null) return null;
		String sub = "";
		if (!Str.isBlank(path)) sub += "/" + path;
		if (!Str.isBlank(relativePath)) sub += "/" + relativePath;
		sub = securePath(sub);
		return new File(baseDir.getPath() + sub);
	}

	private String securePath(String path) {
		if (path == null) return null;
		if (path.equals("..")) return "__";
		path = path.replace("../", "__/");
		path = path.replace("/..", "/__");
		return path;
	}

	public final boolean isAvailable() {
		return getFile(null) != null;
	}

	public final AFileStorage getSubStorage(String path) {
		path = securePath(path);
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
