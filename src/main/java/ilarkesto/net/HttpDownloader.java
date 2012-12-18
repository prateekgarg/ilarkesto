package ilarkesto.net;

import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class HttpDownloader {

	private static final Log log = Log.get(HttpDownloader.class);

	private String charset = IO.ISO_LATIN_1;
	private String username;
	private String password;
	private String baseUrl;

	public String downloadZippedText(String url, String zipContentCharset) {
		url = getFullUrl(url);
		log.info(url);
		InputStream is = null;
		ZipInputStream zis = null;
		try {
			is = IO.openUrlInputStream(url, username, password);
			zis = new ZipInputStream(new BufferedInputStream(is));
			ZipEntry ze;
			try {
				ze = zis.getNextEntry();
			} catch (IOException ex) {
				throw new RuntimeException("Extracting zip file failed: " + url, ex);
			}
			if (ze == null) throw new RuntimeException("Zip file is empty: " + url);
			return IO.readToString(zis, zipContentCharset);
		} finally {
			IO.closeQuiet(zis);
			IO.closeQuiet(is);
		}
	}

	public String downloadText(String url) {
		url = getFullUrl(url);
		log.info(url);
		return IO.downloadUrlToString(url, charset, username, password);
	}

	private String getFullUrl(String url) {
		if (baseUrl == null) return url;
		return baseUrl + url;
	}

	public HttpDownloader setCharset(String charset) {
		this.charset = charset;
		return this;
	}

	public HttpDownloader setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
		return this;
	}

	public HttpDownloader setAuthentication(String username, String password) {
		this.username = username;
		this.password = password;
		return this;
	}

}
