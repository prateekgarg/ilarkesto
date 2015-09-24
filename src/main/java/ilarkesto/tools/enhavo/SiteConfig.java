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

import ilarkesto.io.IO;
import ilarkesto.json.JsonObject;

import java.io.File;

public class SiteConfig {

	private SiteContext context;
	private File file;

	private JsonObject jConfig;
	private JsonObject jProduction;
	private JsonObject jDevelopment;

	public SiteConfig(SiteContext context) {
		super();
		this.context = context;

		file = new File(context.getDir().getPath() + "/config.json");

		boolean save = false;
		if (file.exists()) jConfig = new JsonObject(IO.readFile(file, IO.UTF_8));

		if (jConfig == null) {
			save = true;
			jConfig = new JsonObject();
		}

		save = save | jConfig.putIfNull("productionMode", false);

		jProduction = jConfig.getObjectOrCreate("production");

		jDevelopment = jConfig.getObjectOrCreate("development");
		save = save | jDevelopment.putIfNull("cleanOutputDir", false);

		if (save) IO.writeFile(file, jConfig.toFormatedString(), IO.UTF_8);
	}

	public boolean isCleanOutputDir() {
		if (isProductionMode()) return true;
		return jDevelopment.isTrue("cleanOutputDir");
	}

	public boolean isProductionMode() {
		return jConfig.isTrue("productionMode");
	}

}
