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
package ilarkesto.integration.organizanto;

import ilarkesto.core.base.MapBuilder;
import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;
import ilarkesto.net.HttpDownloader;

import java.io.File;

public class OrganizantoStorage {

	public static final String CHARSET = IO.UTF_8;
	private static final Log log = Log.get(OrganizantoStorage.class);

	private String volume;
	private String accessKey;

	private HttpDownloader http = HttpDownloader.create();

	public OrganizantoStorage(String volume, String accessKey) {
		super();
		this.volume = volume;
		this.accessKey = accessKey;
	}

	public void putFile(String key, File file) {
		String url = Organizanto.URL_SERVICES + "storage.put";
		log.info("putFile()", volume, key, file.getAbsolutePath());
		http.upload(url, file, createParams().put("key", key).getMap(), null, CHARSET);
	}

	public void put(String key, String data) {
		String url = Organizanto.URL_SERVICES + "storage.put";
		log.info("put()", volume, key);
		http.post(url, createParams().put("key", key).put("data", data).getMap(), CHARSET);
	}

	public String get(String key) {
		String url = Organizanto.URL_SERVICES + "storage.get";
		log.info("get()", volume, key);
		String data = http.downloadText(url + createParams().put("key", key).asUrlParams(true), CHARSET);
		return data;
	}

	public void getFile(String key, File destinationFile) {
		String url = Organizanto.URL_SERVICES + "storage.get";
		log.info("get()", volume, key);
		http.downloadUrlToFile(url + createParams().put("key", key).asUrlParams(true), destinationFile);
	}

	private MapBuilder<String, String> createParams() {
		MapBuilder<String, String> params = new MapBuilder<String, String>(true);
		params.put("volume", volume);
		params.put("accessKey", accessKey);
		return params;
	}

}
