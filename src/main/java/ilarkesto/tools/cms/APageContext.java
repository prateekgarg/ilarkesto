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
package ilarkesto.tools.cms;

import ilarkesto.json.JsonObject;

import java.util.HashSet;

public abstract class APageContext extends ABuilder {

	protected SiteContext site;

	public APageContext(SiteContext site) {
		super(site.cms);
		this.site = site;
	}

	protected DataProvider getDataProvider() {
		return site.getDataProvider();
	}

	protected void processData(JsonObject json) {
		DataProvider dataProvider = getDataProvider();

		for (String property : new HashSet<String>(json.getProperties())) {
			if (json.isObject(property)) {
				processData(json.getObject(property));
				continue;
			}

			if (property.startsWith("@")) {
				String dataKey = json.getString(property);
				Object value = dataProvider.get(dataKey);

				String name = property.substring(1);
				json.put(name, value);

				continue;
			}

		}
	}

}
