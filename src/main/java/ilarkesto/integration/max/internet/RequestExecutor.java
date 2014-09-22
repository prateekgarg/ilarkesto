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
package ilarkesto.integration.max.internet;

import ilarkesto.io.IO;

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
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class RequestExecutor {

	private DefaultHttpClient httpClient;

	public RequestExecutor(DefaultHttpClient httpClient) {
		this.httpClient = httpClient;
		// HttpProtocolParams.setUserAgent(httpClient.getParams(),
		// "Mozilla/5.0 (X11; Linux x86_64; rv:7.0.1) Gecko/20100101 Firefox/7.0.1");
		HttpClientParams.setCookiePolicy(httpClient.getParams(), CookiePolicy.BROWSER_COMPATIBILITY);
	}

	public String postAndGetContent(String url, Map<String, String> parameters) {
		HttpResponse response = post(url, parameters);
		return getContent(response);
	}

	public HttpResponse post(String url, Map<String, String> parameters) {
		HttpPost request = new HttpPost(url);
		request.setEntity(createParametersEntity(parameters));

		HttpResponse response;
		try {
			response = httpClient.execute(request);
		} catch (ClientProtocolException ex) {
			throw new RuntimeException(ex);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		System.out.println(response);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 200) throw new RuntimeException("HTTP POST failed: " + response.toString());
		return response;
	}

	public String get(String url) {
		HttpGet request = new HttpGet(url);
		HttpResponse response;
		try {
			response = httpClient.execute(request);
		} catch (ClientProtocolException ex) {
			throw new RuntimeException(ex);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 200) throw new RuntimeException("HTTP GET failed with HTTP Code " + statusCode);
		return getContent(response);
	}

	private String getCookieValue(String name) {
		CookieStore cookieStore = httpClient.getCookieStore();
		for (Cookie cookie : cookieStore.getCookies()) {
			if (name.equals(cookie.getName())) return cookie.getValue();
		}
		return null;
	}

	public String getSessionId() {
		return getCookieValue("JSESSIONID");
	}

	private String getContent(HttpResponse response) {
		HttpEntity entity = response.getEntity();
		return getContent(entity);
	}

	private String getContent(HttpEntity entity) {
		Header encodingHeader = entity.getContentEncoding();
		String charset = encodingHeader == null ? "UTF-8" : encodingHeader.getValue();
		String data;
		try {
			data = IO.readToString(entity.getContent(), charset);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return data;
	}

	private HttpEntity createParametersEntity(Map<String, String> parameters) {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		try {
			return new UrlEncodedFormEntity(pairs, IO.UTF_8);
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}
	}

}
