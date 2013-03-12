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
package ilarkesto.integration.hochschulkompass.pagebuilder;

import ilarkesto.integration.hochschulkompass.Subjectgroup;
import ilarkesto.integration.hochschulkompass.Values;
import ilarkesto.integration.hochschulkompass.ValuesCache;

import java.io.File;

public class Context {

	private File dataDir;
	private ValuesCache valuesCache;

	public Context(File dataDir) {
		super();
		this.dataDir = dataDir;
	}

	public void initialize() {
		getValuesCache().getPayload_ButUpdateIfNull();
	}

	public String href(Subjectgroup subjectgroup) {
		return "subjectgroup.html?key=" + subjectgroup.getKey();
	}

	public Values getValues() {
		return getValuesCache().getPayload_ButUpdateIfNull();
	}

	public ValuesCache getValuesCache() {
		if (valuesCache == null) valuesCache = new ValuesCache(new File(dataDir.getPath() + "/cache/values.json"));
		return valuesCache;
	}

	public String getTitle() {
		return "Studieren - Wo und Was?";
	}

	public String getLanguage() {
		return "de";
	}

}
