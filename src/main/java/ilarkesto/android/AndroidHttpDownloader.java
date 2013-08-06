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
package ilarkesto.android;

import ilarkesto.io.IO;
import ilarkesto.net.HttpDownloader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HttpContext;

import android.net.http.AndroidHttpClient;

public class AndroidHttpDownloader extends HttpDownloader {

	private HttpContext httpContext;

	@Override
	public void downloadUrlToFile(String url, File file) {
		AndroidHttpClient client = createClient();
		BufferedOutputStream out;
		try {
			out = new BufferedOutputStream(new FileOutputStream(file));
		} catch (FileNotFoundException ex) {
			throw new RuntimeException("Writing file failed: " + file, ex);
		}
		try {
			HttpResponse response = client.execute(new HttpGet(url), httpContext);
			HttpEntity entity = response.getEntity();
			entity.writeTo(out);
		} catch (Exception ex) {
			throw new RuntimeException("Downloading failed: " + url, ex);
		} finally {
			IO.close(out);
			client.close();
		}
	}

	@Override
	public String downloadText(String url) {
		AndroidHttpClient client = createClient();
		try {
			HttpResponse response = client.execute(new HttpGet(url), httpContext);
			String text = Android.getText(response, getCharset());
			return text;
		} catch (Exception ex) {
			throw new RuntimeException("Downloading failed: " + url, ex);
		} finally {
			client.close();
		}
	}

	private AndroidHttpClient createClient() {
		return AndroidHttpClient
				.newInstance("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:21.0) Gecko/20100101 Firefox/21.0");
	}

}
