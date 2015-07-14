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
package ilarkesto.gwt.server;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import ilarkesto.core.base.RunnableWithException;
import ilarkesto.core.base.Wrapper;
import ilarkesto.core.logging.Log;
import ilarkesto.core.persistance.Persistence;
import ilarkesto.io.IO;
import ilarkesto.webapp.AWebApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

public abstract class AUploadServlet extends UploadAction {

	protected final Log log = Log.get(getClass());

	protected AWebApplication webApplication;

	protected abstract String handleFiles(HttpServletRequest req, List<FileItem> sessionFiles) throws IOException;

	@Override
	public final String executeAction(final HttpServletRequest req, final List<FileItem> sessionFiles)
			throws UploadActionException {
		// log.info("Upload:\n" + Servlet.toString(req, "  "));

		if (webApplication == null) throw new IllegalStateException("Application not started yet");
		if (webApplication.isShuttingDown())
			throw new IllegalStateException(webApplication.getApplicationLabel() + " shutting down");
		if (webApplication.isStartupFailed())
			throw new IllegalStateException(webApplication.getApplicationLabel() + " startup failed");

		webApplication.getWebSession(req).getContext().bindCurrentThread();

		log.info("Files received:", sessionFiles);
		try {
			synchronized (webApplication.getApplicationLock()) {

				final Wrapper<String> ret = new Wrapper<String>();

				Persistence.runInTransaction(getClass().getSimpleName(), new RunnableWithException() {

					@Override
					public void onRun() throws IOException {
						ret.set(handleFiles(req, new ArrayList<FileItem>(sessionFiles)));
					}
				});

				return ret.get();
			}
		} catch (Exception e) {
			log.error(e);
			throw new UploadActionException(e.getMessage());
		} finally {
			removeSessionFileItems(req);
		}
	}

	@Override
	protected final void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		request.setCharacterEncoding(IO.UTF_8);
		super.doPost(request, response);
	}

	@Override
	public final void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			webApplication = AWebApplication.get();
			if (webApplication == null || webApplication.isStartupFailed())
				throw new RuntimeException("Web application startup failed.");
		} catch (Throwable ex) {
			throw new ServletException(getClass().getSimpleName() + ".init(ServletConfig) failed.", ex);
		}
	}

}
