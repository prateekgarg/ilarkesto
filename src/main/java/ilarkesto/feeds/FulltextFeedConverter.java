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
package ilarkesto.feeds;

import ilarkesto.core.base.Str;
import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;
import ilarkesto.net.ApacheHttpDownloader;
import ilarkesto.net.HttpDownloader;

import java.util.HashSet;
import java.util.Set;

public class FulltextFeedConverter {

	private static final Log log = Log.get(FulltextFeedConverter.class);

	public static void main(String[] args) {
		FulltextFeedConverter converter = new FulltextFeedConverter("http://www.heise.de/newsticker/heise.rdf");
		converter.update();
		System.out.println(converter.feed);
		for (FeedItem item : converter.feed.getItems()) {
			System.out.println(" * " + item);
			System.out.println("    " + item.getDescription());
		}
		System.out.println(converter.feed.createRssText());
	}

	private String url;
	private Feed feed;
	private Set<FeedItem> updatedItems = new HashSet<FeedItem>();

	public static HttpDownloader downloader = new ApacheHttpDownloader();

	public FulltextFeedConverter(String url) {
		super();
		this.url = url;
	}

	public synchronized void update() {
		feed = Feed.load(url);
		log.info("Feed loaded:", feed);
		for (FeedItem item : feed.getItems()) {
			if (updatedItems.contains(item)) continue;
			try {
				replaceDescription(item);
			} catch (Exception ex) {
				log.warn("Replacing feed item description failed:", item.getLink(), ex);
				continue;
			}
			updatedItems.add(item);
		}
	}

	static void replaceDescription(FeedItem item) {
		String link = item.getLink();
		if (Str.isBlank(link)) return;

		String text = downloader.downloadText(link, IO.UTF_8);
		if (Str.isBlank(text)) return;
		text = extract(item, text);
		item.setDescription(text);
	}

	private static String extract(FeedItem item, String text) {
		int idx = -1;

		if ((idx = text.indexOf("<div class=\"meldung_wrapper\">")) > 0) {
			log.debug("heise.de");
			text = text.substring(idx + 29);
			text = Str.removeSuffixStartingWith(text, "</div>");
			return text;
		}

		if ((idx = text.indexOf("<div class=\"article-body\">")) > 0) {
			log.debug("zeit.de");
			text = text.substring(idx);
			text = Str.removeSuffixStartingWith(text, "<a href=\"http://www.zeit.de\"");
			text = Str.removeSuffixStartingWith(text, "<div class=\"articlefooter af\">");
			return text;
		}

		log.warn("Identification failed:", item.getLink());

		if ((idx = text.indexOf("<body>")) > 0) {
			text = text.substring(idx + 6);
			text = Str.removeSuffixStartingWith(text, "</body>");
			return text;
		}

		return text;
	}

	public Feed getFeed() {
		return feed;
	}
}
