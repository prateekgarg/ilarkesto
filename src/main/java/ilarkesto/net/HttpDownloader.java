package ilarkesto.net;

import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;

public class HttpDownloader {

	private static final Log log = Log.get(HttpDownloader.class);

	private String charset = IO.ISO_LATIN_1;
	private String username;
	private String password;
	private String baseUrl;

	public String downloadText(String url) {
		if (baseUrl != null) url = baseUrl + url;
		log.info(url);
		return IO.downloadUrlToString(url, charset, username, password);
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
