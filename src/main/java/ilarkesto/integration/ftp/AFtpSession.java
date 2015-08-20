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
package ilarkesto.integration.ftp;

import ilarkesto.core.auth.LoginDataProvider;
import ilarkesto.core.base.RuntimeTracker;
import ilarkesto.core.logging.Log;

public abstract class AFtpSession implements Runnable {

	protected final Log log = Log.get(getClass());

	protected abstract String getServer();

	protected abstract LoginDataProvider getLogin();

	protected abstract void run(FtpClient ftp);

	@Override
	public final void run() {
		RuntimeTracker rt = new RuntimeTracker();
		FtpClient client = new FtpClient(getServer(), getLogin()).setPort(getPort());
		try {
			run(client);
		} finally {
			client.close();
		}
		log.info("FTP session completed in", rt.getRuntimeFormated());
	}

	protected Integer getPort() {
		return null;
	}

}
