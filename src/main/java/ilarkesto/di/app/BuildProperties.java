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
package ilarkesto.di.app;

import ilarkesto.core.logging.Log;
import ilarkesto.core.time.Time;
import ilarkesto.io.IO;

import java.util.Properties;

public class BuildProperties {

	private Log log = Log.get(BuildProperties.class);

	private Class applicationClass;

	private Properties properties;
	private String releaseLabel;
	private String date;

	public BuildProperties(Class applicationClass) {
		super();
		this.applicationClass = applicationClass;
	}

	public String getReleaseLabel() {
		if (releaseLabel == null) {
			releaseLabel = getProperties().getProperty("release.label");
			if (releaseLabel == null || releaseLabel.equals("@release-label@")) {
				releaseLabel = "dev[" + getBuild() + "]";
			}
		}
		return releaseLabel;
	}

	public String getBuild() {
		if (date == null) {
			date = getProperties().getProperty("date");
			if (date == null || "@build-date@".equals(date)) date = Time.now().toString();
		}
		return date;
	}

	public final Properties getProperties() {
		if (properties == null) {
			try {
				properties = IO.loadProperties(IO.getResource(applicationClass, "build.properties"), IO.UTF_8);
			} catch (Throwable t) {
				log.error(t);
				properties = new Properties();
			}
		}
		return properties;
	}

}
