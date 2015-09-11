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
import ilarkesto.core.base.Utl;
import ilarkesto.core.logging.Log;
import ilarkesto.io.IO.StringInputStream;
import ilarkesto.io.StringOutputStream;
import ilarkesto.json.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class FtpClient {

	private static final Log log = Log.get(FtpClient.class);

	private String server;
	private Integer port;
	private LoginDataProvider login;

	private FTPClient client;

	public FtpClient(String server, LoginDataProvider login) {
		super();
		this.server = server;
		this.login = login;
	}

	public void delete(String path) {
		try {
			client.deleteFile(path);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public List<FTPFile> listFiles() {
		ArrayList<FTPFile> ret = new ArrayList<FTPFile>();
		try {
			for (FTPFile file : client.listFiles()) {
				String name = file.getName();
				if (name.equals(".")) continue;
				if (name.equals("..")) continue;
				ret.add(file);
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		return ret;
	}

	public List<FTPFile> listFilesSortedByTime() {
		List<FTPFile> files = listFiles();
		return Utl.sort(files, FtpClient.FILES_BY_TIME_COMPARATOR);
	}

	public JsonObject downloadJson(String filename) {
		String json = downloadText(filename);
		return new JsonObject(json);
	}

	public String downloadText(String filename) {
		log.debug("download:", filename);
		StringOutputStream out = new StringOutputStream();
		try {
			boolean loaded = client.retrieveFile(filename, out);
			if (!loaded) throw new RuntimeException("Downloading file failed: " + filename);
		} catch (IOException ex) {
			throw new RuntimeException("Downloading file failed: " + filename, ex);
		}
		return out.toString();
	}

	public void uploadText(String path, String text) {
		try {
			client.storeFile(path, new StringInputStream(text));
		} catch (IOException ex) {
			throw new RuntimeException("Uploading failed: " + path, ex);
		}
	}

	public void createDir(String name) {
		log.debug("create dir:", name);
		boolean created;
		try {
			created = client.makeDirectory(name);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		// if (!created) throw new RuntimeException("Creating directory failed: " + name);
	}

	public void changeDir(String path) {
		log.debug("change dir:", path);
		boolean changed;
		try {
			changed = client.changeWorkingDirectory(path);
		} catch (IOException ex) {
			throw new RuntimeException("Changing directory failed: " + path, ex);
		}
		if (!changed) throw new RuntimeException("Changing directory failed: " + path);
	}

	public FtpClient setPort(Integer port) {
		this.port = port;
		return this;
	}

	public synchronized void close() {
		if (client == null) return;
		if (!client.isConnected()) return;
		try {
			client.disconnect();
		} catch (IOException ex) {
			log.error("FTP disconnect failed", ex);
		}
	}

	public synchronized void connect() {
		if (client != null && client.isConnected()) return;

		client = new FTPClient();

		log.info("Connecting", server);
		try {
			client.connect(server, port != null ? port.intValue() : client.getDefaultPort());
			if (!FTPReply.isPositiveCompletion(client.getReplyCode()))
				throw new RuntimeException("Nagative reply after connection");
		} catch (Exception ex) {
			log.error("FTP connection failed:", server, "->", ex);
			return;
		}

		LoginData loginData = login.getLoginData();
		try {
			if (!client.login(loginData.getLogin(), loginData.getPassword()))
				throw new RuntimeException("FTP-Login fehlgeschlagen: " + server);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static Comparator<FTPFile> FILES_BY_TIME_COMPARATOR = new Comparator<FTPFile>() {

		@Override
		public int compare(FTPFile a, FTPFile b) {
			return Utl.compare(a.getTimestamp(), b.getTimestamp());
		}

	};

}
