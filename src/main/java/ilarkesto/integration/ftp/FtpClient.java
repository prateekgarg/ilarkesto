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

import ilarkesto.core.auth.LoginData;
import ilarkesto.core.auth.LoginDataProvider;
import ilarkesto.core.logging.Log;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FtpClient {

	private static final Log log = Log.get(FtpClient.class);

	private String server;
	private Integer port;
	private LoginDataProvider login;

	private FTPClient ftpClient;

	public FtpClient(String server, LoginDataProvider login) {
		super();
		this.server = server;
		this.login = login;
	}

	public FtpClient setPort(Integer port) {
		this.port = port;
		return this;
	}

	public synchronized void close() {
		if (ftpClient == null) return;
		if (!ftpClient.isConnected()) return;
		try {
			ftpClient.disconnect();
		} catch (IOException ex) {
			log.error("FTP disconnect failed", ex);
		}
	}

	private synchronized void ensureFtpConnection() {
		ftpClient = new FTPClient();

		try {
			ftpClient.connect(server, port != null ? port.intValue() : ftpClient.getDefaultPort());
			if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode()))
				throw new RuntimeException("Nagative reply after connection");
		} catch (Exception ex) {
			log.error("FTP connection failed:", server, "->", ex);
			return;
		}

		LoginData loginData = login.getLoginData();
		try {
			if (!ftpClient.login(loginData.getLogin(), loginData.getPassword()))
				throw new RuntimeException("FTP-Login fehlgeschlagen: " + server);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

}
