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

import ilarkesto.integration.jdom.JDom;
import ilarkesto.io.IO;
import ilarkesto.io.StringOutputStream;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;

public class Feed {

	private String title;
	private String link;
	private String description;
	private String language;
	private String pubDate; // RFC 822
	private List<FeedItem> items = new ArrayList<FeedItem>();

	public static Feed load(String url) {
		Document document = JDom.createDocumentFromUrl(url);
		Element eRoot = document.getRootElement();
		Element eChannel = eRoot.getChild("channel");

		String title = JDom.getChildText(eChannel, "title");
		String link = JDom.getChildText(eChannel, "link");
		String description = JDom.getChildText(eChannel, "description");
		Feed feed = new Feed(title, link, description);

		for (Element eItem : eChannel.getChildren("item")) {
			String itemTitle = JDom.getChildText(eItem, "title");
			String itemDescription = JDom.getChildText(eItem, "description");
			FeedItem item = new FeedItem(itemTitle, itemDescription);
			feed.addItem(item);
			item.setLink(JDom.getChildText(eItem, "link"));
			item.setPubDate(JDom.getChildText(eItem, "pubDate"));
			item.setGuid(JDom.getChildText(eItem, "guid"));
		}

		return feed;
	}

	public Feed(String title, String link, String description) {
		super();
		this.title = title;
		this.link = link;
		this.description = description;
	}

	public Document createRssJDom() {
		Document document = new Document();
		Element eRoot = new Element("rss");
		eRoot.setAttribute("version", "2.0");
		document.setRootElement(eRoot);

		Element eChannel = JDom.addElement(eRoot, "channel");
		JDom.addTextElement(eChannel, "title", title);
		JDom.addTextElement(eChannel, "link", link);
		JDom.addTextElement(eChannel, "description", description);
		if (language != null) JDom.addTextElement(eChannel, "language", language);
		if (pubDate != null) JDom.addTextElement(eChannel, "pubDate", pubDate);

		for (FeedItem item : items) {
			item.appendTo(eChannel);
		}

		return document;
	}

	public String createRssText() {
		StringOutputStream out = new StringOutputStream();
		writeRss(out);
		return out.toString();
	}

	public void writeRss(OutputStream out) {
		JDom.write(createRssJDom(), out, IO.UTF_8);
	}

	public void addItem(FeedItem item) {
		items.add(item);
	}

	public String getTitle() {
		return title;
	}

	public String getLink() {
		return link;
	}

	public String getDescription() {
		return description;
	}

	public String getLanguage() {
		return language;
	}

	public List<FeedItem> getItems() {
		return items;
	}

	public String getPubDate() {
		return pubDate;
	}

	@Override
	public String toString() {
		return title;
	}

}
