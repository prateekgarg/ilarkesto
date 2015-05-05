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

import org.jdom2.Element;

public class FeedItem {

	private String title;
	private String description;
	private String link;
	private String guid;
	private String pubDate;

	public FeedItem(String title, String description) {
		super();
		this.title = title;
		this.description = description;
	}

	void appendTo(Element eChannel) {
		Element eItem = JDom.addElement(eChannel, "item");
		if (title != null) JDom.addTextElement(eItem, "title", title);
		if (description != null) JDom.addTextElement(eItem, "description", description);
		if (pubDate != null) JDom.addTextElement(eItem, "pubDate", pubDate);
		if (link != null) JDom.addTextElement(eItem, "link", link);
		if (guid != null) JDom.addTextElement(eItem, "guid", guid);
	}

	public FeedItem setDescription(String description) {
		this.description = description;
		return this;
	}

	public FeedItem setLink(String link) {
		this.link = link;
		return this;
	}

	public FeedItem setGuid(String guid) {
		this.guid = guid;
		return this;
	}

	public FeedItem setPubDate(String pubDate) {
		this.pubDate = pubDate;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getLink() {
		return link;
	}

	public String getGuid() {
		return guid;
	}

	public String getPubDate() {
		return pubDate;
	}

	@Override
	public String toString() {
		return title == null ? description : title;
	}

}
