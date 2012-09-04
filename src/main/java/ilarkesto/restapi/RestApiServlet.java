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

import ilarkesto.base.Sys;
import ilarkesto.core.base.Str;
import ilarkesto.core.logging.Log;
import ilarkesto.json.JsonObject;
import ilarkesto.webapp.AServlet;
import ilarkesto.webapp.AWebApplication;
import ilarkesto.webapp.Servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RestApiServlet extends AServlet<AWebApplication> {

	private static Log log = Log.get(RestApiServlet.class);

	@Override
	protected void onGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		ARestApi api = getApi(req);
		if (api == null) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		writeGet(resp, api);
	}

	@Override
	protected void onPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		ARestApi api = getApi(req);
		if (api == null) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		update(req, api);
		writeGet(resp, api);
	}

	private void update(HttpServletRequest req, ARestApi api) {
		String s = Servlet.readContentToString(req);
		if (Str.isBlank(s)) throw new RuntimeException("Illegal request");
		JsonObject json = new JsonObject(s);
		log.info(json.toFormatedString());
		api.post(json);
	}

	private void writeGet(HttpServletResponse resp, ARestApi api) throws IOException {
		JsonObject result = api.get();
		result.write(resp.getWriter(), Sys.isDevelopmentMode());
	}

	private ARestApi getApi(HttpServletRequest req) {
		String path = Str.cutFrom(Servlet.getUriWithoutContext(req), "api/");
		log.info(path);
		return webApplication.getRestApiFactory().createApi(req, path);
	}

}
