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
package ilarkesto.integration.testde;

import ilarkesto.core.base.OperationObserver;
import ilarkesto.core.base.Parser;
import ilarkesto.core.base.Parser.ParseException;
import ilarkesto.core.time.Date;
import ilarkesto.io.IO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TestDe {

	public static final String URL_BASE = "https://www.test.de";
	public static final String URL_TEST_INDEX = URL_BASE + "/tests/";

	private static final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

	public static Article downloadArticle(ArticleRef ref, OperationObserver observer) {
		String url = getArticleUrl(ref);
		observer.onOperationInfoChanged(OperationObserver.DOWNLOADING, url);
		String data = IO.downloadUrlToString(url);

		return new Article(ref);
	}

	public static String getArticleUrl(ArticleRef ref) {
		return URL_BASE + "/" + ref.getPageId() + "/";
	}

	public static List<ArticleRef> downloadNewArticleRefs(List<ArticleRef> lastKnown, OperationObserver observer)
			throws ParseException {
		List<ArticleRef> ret = new ArrayList<TestDe.ArticleRef>();
		int offset = 1;
		while (true) {
			List<ArticleRef> newArticles = downloadArticleRefs(offset, observer);
			if (newArticles.isEmpty()) return ret;
			for (ArticleRef ref : newArticles) {
				if (ref.equals(lastKnown)) return ret;
				ret.add(ref);
			}
			offset++;
		}
	}

	public static List<ArticleRef> downloadArticleRefs(int indexOffset, OperationObserver observer)
			throws ParseException {
		if (indexOffset == 0) throw new IllegalArgumentException("page 0 does not exist");
		String url = URL_TEST_INDEX + "?fd=" + indexOffset;
		observer.onOperationInfoChanged(OperationObserver.DOWNLOADING, url);
		String data = IO.downloadUrlToString(url);

		ArrayList<ArticleRef> ret = new ArrayList<ArticleRef>();
		Parser parser = new Parser(data);
		parser.gotoAfter("<ul class=\"search-result-list\">");
		while (parser.gotoAfterIfNext("<li>")) {
			parser.gotoAfter("<a href=\"/");
			String pageId = parser.getUntil("/\"");
			parser.gotoAfter("<span class=\"date\">");
			String dateS = parser.getUntil("</span>");
			Date date;
			try {
				date = new Date(dateFormat.parse(dateS));
			} catch (java.text.ParseException ex) {
				throw new ParseException("Unexpected date format: " + dateS);
			}
			parser.gotoAfter("<h3>");
			String title = parser.getUntil("</h3>");
			parser.gotoAfter("</li>");
			ArticleRef articleRef = new ArticleRef(date, title, pageId);
			ret.add(articleRef);
		}

		return ret;
	}

	public static class Article {

		private ArticleRef ref;
		private String pdfUrl;

		public Article(ArticleRef ref) {
			super();
			this.ref = ref;
		}

		@Override
		public int hashCode() {
			return ref.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Article)) return false;
			return ref.equals(((Article) obj).ref);
		}

		@Override
		public String toString() {
			return ref.toString();
		}

	}

	public static class ArticleRef implements Comparable<ArticleRef> {

		private String title;
		private Date date;
		private String pageId;

		public ArticleRef(Date date, String title, String pageId) {
			super();
			this.date = date;
			this.title = title;
			this.pageId = pageId;
		}

		public Date getDate() {
			return date;
		}

		public String getTitle() {
			return title;
		}

		public String getPageId() {
			return pageId;
		}

		@Override
		public int compareTo(ArticleRef other) {
			return other.getDate().compareTo(getDate());
		}

		@Override
		public int hashCode() {
			return pageId.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ArticleRef)) return false;
			return pageId.equals(((ArticleRef) obj).pageId);
		}

		@Override
		public String toString() {
			return getDate() + " " + getTitle();
		}
	}

}
