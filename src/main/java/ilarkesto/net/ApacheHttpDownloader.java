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
package ilarkesto.net;

import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class ApacheHttpDownloader extends HttpDownloader {

	private static final Log log = Log.get(ApacheHttpDownloader.class);

	private HttpClient client;
	private HttpContext context;

	@Override
	public String post(String url, Map<String, String> parameters, Map<String, String> requestHeaders, String charset) {
		HttpResponse response;
		try {
			response = doPost(url, parameters, requestHeaders, charset);
		} catch (HttpRedirectException ex) {
			return downloadText(ex.getLocation(), charset);
		}
		try {
			return getText(response);
		} catch (IOException ex) {
			throw new RuntimeException("HTTP POST failed.", ex);
		}
	}

	private synchronized HttpResponse doPost(String url, Map<String, String> parameters,
			Map<String, String> requestHeaders, String charset) throws HttpRedirectException {
		HttpPost request = new HttpPost(url);
		if (requestHeaders != null) {
			for (Map.Entry<String, String> requestHeader : requestHeaders.entrySet()) {
				request.addHeader(requestHeader.getKey(), requestHeader.getValue());
			}
		}
		request.setEntity(createParametersEntity(parameters, charset));

		// if (true) {
		// Log.TEST(request.toString());
		// try {
		// request.getEntity().writeTo(new PrintStream(System.out));
		// } catch (IOException ex) {
		// throw new RuntimeException(ex);
		// }
		// }

		HttpClient client = getClient();
		try {
			HttpResponse response = client.execute(request, getContext());
			int statusCode = response.getStatusLine().getStatusCode();
			if (isHttpStatusCodeRedirect(statusCode)) {
				getText(response);
				throw new HttpRedirectException(getRedirectLocation(response));
			}
			if (statusCode != 200)
				throw new RuntimeException("HTTP POST failed. HTTP Status " + statusCode + " -> " + getText(response));
			return response;
		} catch (ClientProtocolException ex) {
			throw new RuntimeException(ex);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} finally {
			close(client);
		}
	}

	private boolean isHttpStatusCodeRedirect(int statusCode) {
		return statusCode == 301 || statusCode == 302;
	}

	private HttpEntity createParametersEntity(Map<String, String> parameters, String charset) {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		try {
			return new UrlEncodedFormEntity(pairs, charset);
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public synchronized void downloadUrlToFile(String url, File file, int followRedirects) {
		file.getParentFile().mkdirs();
		BufferedOutputStream out = null;
		HttpClient client = getClient();
		try {
			HttpResponse response = client.execute(new HttpGet(url), getContext());
			int statusCode = response.getStatusLine().getStatusCode();
			if (isHttpStatusCodeRedirect(statusCode)) {
				String location = getRedirectLocation(response);
				if (followRedirects > 0) {
					log.info("HTTP Redirect:", location);
					downloadUrlToFile(location, file, followRedirects - 1);
					return;
				}
				getText(response);
				throw new HttpRedirectException(location);
			}
			if (statusCode != 200) {
				String text = getText(response);
				throw new RuntimeException("HTTP GET failed. HTTP Status " + statusCode + " -> " + text);
			}
			HttpEntity entity = response.getEntity();
			try {
				out = new BufferedOutputStream(new FileOutputStream(file));
			} catch (FileNotFoundException ex) {
				throw new RuntimeException("Writing file failed: " + file, ex);
			}
			entity.writeTo(out);
		} catch (Exception ex) {
			throw new RuntimeException("Downloading failed: " + url, ex);
		} finally {
			IO.close(out);
			close(client);
		}
	}

	private String getRedirectLocation(HttpResponse response) {
		String location = getHeader(response, "Location");
		if (location == null) return null;
		// location = location.replace("&amp;", "&");
		return location;
	}

	@Override
	public synchronized String downloadText(String url, String charset, int followRedirects) {
		HttpClient client = getClient();
		try {
			HttpResponse response = client.execute(new HttpGet(url), getContext());
			int statusCode = response.getStatusLine().getStatusCode();
			String text = getText(response, charset);
			if (isHttpStatusCodeRedirect(statusCode)) {
				String location = getRedirectLocation(response);
				if (followRedirects > 0) {
					log.info("HTTP Redirect:", location);
					return downloadText(location, charset, followRedirects - 1);
				}
				return text;
			}
			if (statusCode != 200)
				throw new RuntimeException("HTTP GET failed. HTTP Status " + statusCode + " -> " + text);
			return text;
		} catch (Exception ex) {
			throw new RuntimeException("Downloading failed: " + url, ex);
		} finally {
			close(client);
		}
	}

	public HttpContext getContext() {
		if (context == null) {
			context = new BasicHttpContext();
			context.setAttribute(ClientContext.COOKIE_STORE, new BasicCookieStore());
		}
		return context;
	}

	public synchronized HttpClient getClient() {
		if (client == null) client = createClient();
		return client;
	}

	protected HttpClient createClient() {
		DefaultHttpClient client = new DefaultHttpClient();
		initializeClient(client);
		return client;
	}

	protected void initializeClient(HttpClient client) {
		HttpParams params = client.getParams();
		HttpClientParams.setCookiePolicy(params, CookiePolicy.BROWSER_COMPATIBILITY);
		HttpClientParams.setRedirecting(params, false);
	}

	protected synchronized void close(HttpClient client) {
		client = null;
	}

	// --- helper ---

	public static String getText(HttpResponse resp) throws IOException {
		HttpEntity entity = resp.getEntity();
		Header encodingHeader = entity.getContentEncoding();
		return getText(entity, encodingHeader == null ? IO.UTF_8 : encodingHeader.getValue());
	}

	public static String getText(HttpResponse resp, String charsetName) throws IOException {
		return getText(resp.getEntity(), charsetName);
	}

	private static String getText(HttpEntity entity, String charsetName) throws IOException,
			UnsupportedEncodingException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		entity.writeTo(buffer);
		return buffer.toString(charsetName == null ? IO.UTF_8 : charsetName);
	}

	public static String getHeader(HttpResponse response, String name) {
		Header[] headers = response.getHeaders(name);
		if (headers == null || headers.length == 0) return null;
		return headers[0].getValue();
	}

	// ---

	public static class HttpRedirectException extends Exception {

		public HttpRedirectException(String location) {
			super(location);
		}

		public String getLocation() {
			return getMessage();
		}

	}

}
