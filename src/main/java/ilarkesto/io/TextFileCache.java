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

import ilarkesto.core.base.OperationObserver;
import ilarkesto.core.base.Str;

import java.io.File;

public class TextFileCache {

	private AFileStorage storage;
	private Loader loader;

	public TextFileCache(AFileStorage storage, Loader loader) {
		super();
		this.storage = storage;
		this.loader = loader;
	}

	public synchronized String load(String key, OperationObserver operationObserver) {
		String text = loadFromCache(key, operationObserver);

		if (text != null) return text;

		text = loader.load(key, operationObserver);

		File file = getFile(key);
		operationObserver.onOperationInfoChanged(OperationObserver.SAVING, file);
		IO.writeFile(file, text, IO.UTF_8);
		return text;
	}

	public synchronized String loadFromCache(String key, OperationObserver operationObserver) {
		File file = getFile(key);

		if (!file.exists()) return null;

		operationObserver.onOperationInfoChanged(OperationObserver.LOADING_CACHE);
		return IO.readFile(file, IO.UTF_8);
	}

	public synchronized void delete(String key) {
		IO.delete(getFile(key));
	}

	private File getFile(String key) {
		return storage.getFile(Str.toFileCompatibleString(key) + ".cache.txt");
	}

	public static interface Loader {

		String load(String key, OperationObserver operationObserver);

	}

}
