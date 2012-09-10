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

import javax.servlet.http.HttpServletRequest;

public class RequestParametersWrapper {

	private HttpServletRequest req;

	public RequestParametersWrapper(HttpServletRequest req) {
		super();
		this.req = req;
	}

	public String get(String name) {
		return req.getParameter(name);
	}

	public String get(String name, String defaultValue) {
		String value = get(name);
		return value == null ? defaultValue : value;
	}

	public String getMandatory(String name) {
		String value = get(name);
		if (value == null) throw new RuntimeException("Missing mandatory request parameter: " + name);
		return value;
	}

}
