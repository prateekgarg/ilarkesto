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

import ilarkesto.core.base.Str;
import ilarkesto.core.logging.Log;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AServlet<A extends AWebApplication> extends HttpServlet {

	private static Log log = Log.get(AServlet.class);

	protected A webApplication;

	protected void onGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.sendError(HttpServletResponse.SC_NO_CONTENT);
	}

	protected void onPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.sendError(HttpServletResponse.SC_NO_CONTENT);
	}

	@Override
	protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		AWebSession session = webApplication.getWebSession(req);
		session.getContext().bindCurrentThread();
		try {
			onGet(req, resp);
		} catch (Throwable ex) {
			handleError(ex, req, resp);
		}
	}

	@Override
	protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		AWebSession session = webApplication.getWebSession(req);
		session.getContext().bindCurrentThread();
		try {
			onPost(req, resp);
		} catch (Throwable ex) {
			handleError(ex, req, resp);
		}
	}

	private void handleError(Throwable ex, HttpServletRequest req, HttpServletResponse resp) {
		log.info("request caused error:", req.getRequestURI(), ex);
		try {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Str.format(ex));
		} catch (IOException ex1) {
			log.error(ex1);
		}
	}

	@Override
	public final void init(ServletConfig config) throws ServletException {
		super.init(config);
		webApplication = (A) AWebApplication.get();
	}

}
