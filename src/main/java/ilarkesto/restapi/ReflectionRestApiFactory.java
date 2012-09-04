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
package ilarkesto.restapi;

import ilarkesto.base.Reflect;
import ilarkesto.base.Str;
import ilarkesto.webapp.AWebApplication;
import ilarkesto.webapp.AWebSession;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class ReflectionRestApiFactory implements RestApiFactory {

	private AWebApplication webApplication;
	private List<String> packages = new ArrayList<String>();

	public ReflectionRestApiFactory(AWebApplication webApplication) {
		this.webApplication = webApplication;
		addPackage(webApplication.getClass().getPackage());
	}

	public void addPackage(Package pkg) {
		addPackage(pkg.getName());
	}

	public void addPackage(String packageName) {
		packages.add(packageName);
	}

	@Override
	public ARestApi createApi(HttpServletRequest req, String path) {
		ARestApi api = createApiInstance(path);
		if (api == null) return null;

		AWebSession session = webApplication.getWebSession(req);
		session.getContext().autowire(api);

		return api;
	}

	private ARestApi createApiInstance(String path) {
		String classSimpleName = path.isEmpty() ? "RootApi" : Str.uppercaseFirstLetter(path) + "Api";
		for (String pkg : packages) {
			String className = pkg + "." + classSimpleName;
			Class<? extends ARestApi> type;
			try {
				type = (Class<? extends ARestApi>) Class.forName(className);
				return Reflect.newInstance(type);
			} catch (ClassNotFoundException ex) {
				continue;
			}
		}
		return null;
	}

}
