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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;

public class ApacheHttpDownloader extends HttpDownloader {

	private HttpClient client;

	@Override
	public String post(String url, Map<String, String> parameters, Map<String, String> requestHeaders, String charset) {
		HttpResponse response = doPost(url, parameters, requestHeaders, charset);
		try {
			return getText(response);
		} catch (IOException ex) {
			throw new RuntimeException("HTTP POST failed.", ex);
		}
	}

	private HttpResponse doPost(String url, Map<String, String> parameters, Map<String, String> requestHeaders,
			String charset) {
		HttpClient client = getClient();

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

		try {
			HttpResponse response = client.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) throw new RuntimeException("HTTP POST failed: " + getText(response));
			return response;
		} catch (ClientProtocolException ex) {
			throw new RuntimeException(ex);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
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
	public void downloadUrlToFile(String url, File file) {
		HttpClient client = getClient();
		BufferedOutputStream out;
		try {
			out = new BufferedOutputStream(new FileOutputStream(file));
		} catch (FileNotFoundException ex) {
			throw new RuntimeException("Writing file failed: " + file, ex);
		}
		try {
			HttpResponse response = client.execute(new HttpGet(url));
			HttpEntity entity = response.getEntity();
			entity.writeTo(out);
		} catch (Exception ex) {
			throw new RuntimeException("Downloading failed: " + url, ex);
		} finally {
			IO.close(out);
		}
	}

	@Override
	public String downloadText(String url, String charset) {
		HttpClient client = getClient();
		try {
			HttpResponse response = client.execute(new HttpGet(url));
			String text = getText(response, charset);
			return text;
		} catch (Exception ex) {
			throw new RuntimeException("Downloading failed: " + url, ex);
		}
	}

	public HttpClient getClient() {
		if (client == null) client = createClient();
		return client;
	}

	protected HttpClient createClient() {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpParams params = client.getParams();
		HttpClientParams.setCookiePolicy(params, CookiePolicy.BROWSER_COMPATIBILITY);
		return client;
	}

	protected void close(HttpClient client) {
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

}
