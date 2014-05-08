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
package ilarkesto.webapp;

import ilarkesto.core.logging.Log;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class WebappListener implements ServletContextListener {

	private static final Log log = Log.get(WebappListener.class);

	@Override
	public void contextInitialized(ServletContextEvent event) {
		log.info("webapp context initialized");
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		log.info("webapp context destroyed");
		AWebApplication.get().shutdown();
	}

}
