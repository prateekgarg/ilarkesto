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
package ilarkesto.tools.enhavo;

import ilarkesto.core.base.Str;
import ilarkesto.io.IO;
import ilarkesto.json.JsonObject;

import java.io.File;

public class FilesDataProvider extends ADataProvider {

	private File dir;

	public FilesDataProvider(File dir, DataProvider fallback) {
		super(fallback);
		this.dir = dir;
	}

	@Override
	protected Object onGet(String key) {
		File file = new File(dir.getPath() + "/" + Str.securePath(key));
		return createObject(file);
	}

	private Object createObject(File file) {
		if (file.isDirectory()) return createJsonFromDir(file);

		String name = file.getName();
		if (name.endsWith(".json")) return JsonObject.loadFile(file, false);
		return IO.readFile(file);
	}

	private JsonObject createJsonFromDir(File dir) {
		JsonObject ret = new JsonObject();
		File[] files = dir.listFiles();
		for (File file : files) {
			Object object = createObject(file);
			String property = file.getName();
			Str.removeSuffixStartingWithLast(property, ".");
			ret.put(property, object);
		}
		return ret;
	}
}
