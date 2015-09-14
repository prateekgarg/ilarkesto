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
import ilarkesto.core.base.Filepath;
import ilarkesto.core.base.Str;
import ilarkesto.core.base.Utl;
import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;
import ilarkesto.io.IO.StringInputStream;
import ilarkesto.io.StringOutputStream;
import ilarkesto.json.JsonObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
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

	public List<FTPFile> listFiles(String path) {
		ArrayList<FTPFile> ret = new ArrayList<FTPFile>();
		try {
			for (FTPFile file : client.listFiles(path)) {
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

	public List<FTPFile> listFilesSortedByTime(String path) {
		List<FTPFile> files = listFiles(path);
		return Utl.sort(files, FtpClient.FILES_BY_TIME_COMPARATOR);
	}

	public JsonObject downloadJson(String path) {
		String json = downloadText(path);
		return new JsonObject(json);
	}

	public String downloadText(String path) {
		log.debug("download:", path);
		StringOutputStream out = new StringOutputStream();
		try {
			boolean loaded = client.retrieveFile(path, out);
			if (!loaded) throw new RuntimeException("Downloading file failed: " + path);
		} catch (IOException ex) {
			throw new RuntimeException("Downloading file failed: " + path, ex);
		}
		return out.toString();
	}

	public void uploadText(String path, String text) {
		log.debug("Upload:", path);
		try {
			client.storeFile(path, new StringInputStream(text));
		} catch (IOException ex) {
			throw new RuntimeException("Uploading failed: " + path, ex);
		}
	}

	public void uploadFile(String path, File file) {
		log.debug("Upload:", path);
		if (!file.exists()) return;

		if (file.isDirectory()) { throw new IllegalStateException("Uploading file failed. File is a directory: "
				+ file.getAbsolutePath()); }

		try {
			client.storeFile(path, new BufferedInputStream(new FileInputStream(file)));
		} catch (IOException ex) {
			throw new RuntimeException("Uploading failed: " + path + " <- " + file.getAbsolutePath(), ex);
		}
	}

	public void uploadFiles(String path, File[] files) {
		if (!Str.isBlank(path)) createDir(path);
		for (File file : files) {
			String filePath = Str.isBlank(path) ? file.getName() : path + "/" + file.getName();
			if (file.isDirectory()) {
				createDir(filePath);
				uploadFiles(filePath, file.listFiles());
			} else {
				uploadFile(filePath, file);
			}
		}
	}

	public void uploadFileIfNotThere(String path, File file) {
		log.debug("Upload:", path);
		if (!file.exists()) return;

		FTPFile ftpFile = getFile(path);
		if (ftpFile != null) {
			log.debug("  Skipping upload, already there:", path);
			return;
		}

		try {
			client.storeFile(path, new BufferedInputStream(new FileInputStream(file)));
		} catch (IOException ex) {
			throw new RuntimeException("Uploading failed: " + path + " <- " + file.getAbsolutePath(), ex);
		}
	}

	public void createDir(String path) {
		if (existsDir(path)) return;
		log.debug("create dir:", path);
		boolean created;
		try {
			created = client.makeDirectory(path);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		if (!created) throw new RuntimeException("Creating directory failed: " + path);
	}

	// public void changeDir(String path) {
	// log.debug("change dir:", path);
	// boolean changed;
	// try {
	// changed = client.changeWorkingDirectory(path);
	// } catch (IOException ex) {
	// throw new RuntimeException("Changing directory failed: " + path, ex);
	// }
	// if (!changed) throw new RuntimeException("Changing directory failed: " + path);
	// }

	public boolean existsFileOrDir(String path) {
		return getFile(path) != null;
	}

	public boolean existsDir(String path) {
		FTPFile file = getFile(path);
		if (file == null) return false;
		return file.isDirectory();
	}

	public FTPFile getFile(String path) {
		Filepath filepath = new Filepath(path);
		String parentPath = filepath.getParentAsString();
		String name = filepath.getLastElementName();
		for (FTPFile ftpFile : listFiles(parentPath)) {
			if (ftpFile.getName().equals(name)) return ftpFile;
		}
		return null;
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

		client.setAutodetectUTF8(false);
		client.setCharset(Charset.forName(IO.UTF_8));
	}

	public static Comparator<FTPFile> FILES_BY_TIME_COMPARATOR = new Comparator<FTPFile>() {

		@Override
		public int compare(FTPFile a, FTPFile b) {
			return Utl.compare(a.getTimestamp(), b.getTimestamp());
		}

	};

}
