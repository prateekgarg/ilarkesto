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
package ilarkesto.integration.wikimedia;

import ilarkesto.core.base.OperationObserver;
import ilarkesto.core.base.Parser;
import ilarkesto.core.base.Parser.ParseException;
import ilarkesto.core.base.Utl;
import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;
import ilarkesto.json.JsonObject;
import ilarkesto.net.HttpDownloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WikiLoader {

	private static final Log log = Log.get(WikiLoader.class);

	private static final String charset = IO.ISO_LATIN_1;

	public static final String BASE_URL_WIKIPEDIA_EN = "https://en.wikipedia.org/";
	public static final String BASE_URL_WIKIPEDIA_DE = "https://de.wikipedia.org/";

	private HttpDownloader httpDownloader;
	private String baseUrl;

	public WikiLoader(HttpDownloader httpDownloader, String baseUrl) {
		super();
		this.httpDownloader = httpDownloader;
		this.baseUrl = baseUrl;
	}

	public Map<String, String> loadInfobox(long pageId, OperationObserver observer) {
		String content = loadPageContent(pageId, false, false, observer);
		return extractInfobox(content);
	}

	public static Map<String, String> extractInfobox(String content) {
		Parser parser = new Parser(content);
		if (!parser.gotoAfterIf("{{Infobox")) return null;
		Map<String, String> map = new HashMap<String, String>();
		do {
			try {
				parser.gotoAfter("\n|");
				String key = parser.getUntilAndGotoAfter("=").trim();
				String value = parser.getUntil("\n");
				parser.skip(value.length());
				value = value.trim();
				value = extractTextFromWikiCode(value);
				value = value.replace("\n", " ");
				map.put(key, value);
			} catch (ParseException ex) {
				throw new RuntimeException(ex);
			}
		} while (parser.isNext("\n|"));
		return map;
	}

	public static String extractTextFromWikiCode(String code) {
		if (code == null) return null;
		code = code.replace("[[", "");
		code = code.replace("]]", "");
		code = code.replace("<br />", "\n");
		code = code.replace("<br/>", "\n");
		code = code.replace("<br>", "\n");
		return code;
	}

	public String loadPageContent(long pageId, boolean html, boolean followRedirect, OperationObserver observer) {
		String url = getApiUrl() + "?action=query&prop=revisions&pageids=" + pageId + "&rvprop=content&format=json";
		if (html) url += "&rvparse=1";
		if (followRedirect) url += "&redirects=1";
		observer.onOperationInfoChanged(OperationObserver.DOWNLOADING, url);
		String data = httpDownloader.downloadText(url, charset);
		JsonObject json = new JsonObject(data);
		JsonObject query = json.getObject("query");
		JsonObject pages = query.getObject("pages");
		JsonObject page = pages.getObject(Utl.getFirstElement(pages.getProperties()));
		List<JsonObject> revisions = page.getArrayOfObjects("revisions");
		return revisions.get(0).getString("*");
	}

	public List<PageRef> loadCategoryMembers(long categoryPageId, OperationObserver observer) {
		List<PageRef> ret = new ArrayList<PageRef>();
		String continueToken = null;
		do {
			JsonObject json = loadQuery("&list=categorymembers&cmlimit=max&cmtype=page&cmpageid=" + categoryPageId,
				continueToken, observer);
			for (JsonObject jPage : json.getObject("query").getArrayOfObjects("categorymembers")) {
				PageRef page = new PageRef(jPage.getLong("pageid"), jPage.getString("title"));
				ret.add(page);
			}
			continueToken = json.getDeepString("query-continue", "categorymembers", "cmcontinue");
		} while (continueToken != null);
		return ret;
	}

	private JsonObject loadQuery(String urlSuffix, String continueToken, OperationObserver observer) {
		String url = getApiUrl() + "?action=query&format=json";
		if (continueToken != null) url += "&cmcontinue=" + continueToken;
		if (urlSuffix != null) url += urlSuffix;
		observer.onOperationInfoChanged(OperationObserver.DOWNLOADING, url);
		String data = httpDownloader.downloadText(url, charset);
		JsonObject json = new JsonObject(data);
		log.debug("Loaded", url, "->", json.toFormatedString());
		return json;
	}

	public String getApiUrl() {
		return baseUrl + "/w/api.php";
	}

}
