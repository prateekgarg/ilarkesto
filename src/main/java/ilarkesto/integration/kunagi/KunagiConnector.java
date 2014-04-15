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
package ilarkesto.integration.kunagi;

import ilarkesto.core.base.OperationObserver;
import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;
import ilarkesto.net.ApacheHttpDownloader;
import ilarkesto.net.HttpDownloader;

import java.util.HashMap;
import java.util.Map;

public class KunagiConnector {

	private static Log log = Log.get(KunagiConnector.class);

	private String kunagiUrl;
	private String projectId;
	private HttpDownloader httpDownloader = new ApacheHttpDownloader();

	public KunagiConnector(String kunagiUrl, String projectId) {
		super();
		this.kunagiUrl = kunagiUrl;
		this.projectId = projectId;
	}

	public KunagiConnector(String projectId) {
		this("https://servisto.de/kunagi/", projectId);
	}

	public String postIssue(OperationObserver observer, String name, String email, String subject, String text,
			boolean wiki, boolean publish) {
		String url = kunagiUrl + "submitIssue";
		observer.onOperationInfoChanged(OperationObserver.DOWNLOADING, url);
		Map<String, String> data = new HashMap<String, String>();
		data.put("projectId", projectId);
		data.put("name", name);
		data.put("email", email);
		data.put("publish", String.valueOf(publish));
		data.put("wiki", String.valueOf(wiki));
		data.put("subject", subject);
		data.put("text", text);
		data.put("spamPreventionCode", "no-spam");
		String ret = httpDownloader.post(url, data, IO.UTF_8);
		if (ret.contains("Submitting issue failed") || ret.contains("Submitting your feedback failed")) {
			log.error("Submitting issue failed:", ret);
			throw new RuntimeException(ret);
		}
		log.info("Issue submitted:", ret);
		return ret;
	}

	public KunagiConnector setHttpDownloader(HttpDownloader httpDownloader) {
		this.httpDownloader = httpDownloader;
		return this;
	}

}
