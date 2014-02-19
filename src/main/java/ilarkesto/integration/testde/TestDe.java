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
import ilarkesto.core.base.Str;
import ilarkesto.core.logging.Log;
import ilarkesto.core.time.Date;
import ilarkesto.io.IO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TestDe {

	private static final Log log = Log.get(TestDe.class);

	public static final String URL_BASE = "https://www.test.de";
	public static final String URL_TEST_INDEX = URL_BASE + "/tests/";

	private static final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

	public static Article downloadArticle(ArticleRef ref, OperationObserver observer) throws ParseException {
		String url = getArticleUrl(ref);
		observer.onOperationInfoChanged(OperationObserver.DOWNLOADING, url);
		String data = IO.downloadUrlToString(url);

		String navigData = Str.cutFromTo(data, "<ol class=\"articlenav-nav\">", "</ol>");
		Parser parser = new Parser(navigData);
		parser.skipWhitespace();
		List<SubArticleRef> subArticles = new ArrayList<TestDe.SubArticleRef>();
		while (parser.gotoAfterIf("<li")) {
			parser.gotoAfter("<a ");
			parser.gotoAfter("href=\"");
			String pageRef;
			if (parser.isNext("/")) {
				parser.gotoAfter("/");
				pageRef = parser.getUntil("/\"");
			} else {
				pageRef = parser.getUntil("\"");
			}
			parser.gotoAfter(">");
			String title = parser.getUntil("</a>");
			parser.gotoAfter("</li>");
			SubArticleRef subArticleRef = new SubArticleRef(title, pageRef);
			subArticles.add(subArticleRef);
		}

		return new Article(ref, subArticles);
	}

	public static String getArticleUrl(ArticleRef ref) {
		return URL_BASE + "/" + ref.getPageRef() + "/";
	}

	public static boolean update(ArticlesIndex index, OperationObserver observer) throws ParseException {
		List<ArticleRef> newArticles = downloadNewArticleRefs(index.getArticles(), observer);
		if (newArticles.isEmpty()) return false;
		index.addNewArticles(newArticles);
		return true;
	}

	static List<ArticleRef> downloadNewArticleRefs(Collection<ArticleRef> knownArticles, OperationObserver observer)
			throws ParseException {
		Set<String> knownArticleIds = new HashSet<String>();
		Date newest = new Date(1999, 1, 1);
		for (ArticleRef articleRef : knownArticles) {
			if (articleRef.getDate().isAfter(newest)) newest = articleRef.getDate();
			knownArticleIds.add(articleRef.getPageId());
		}
		log.info("Downloading new articles. Known:", knownArticleIds.size(), "->", knownArticleIds);
		List<ArticleRef> ret = new ArrayList<TestDe.ArticleRef>();
		int offset = 1;
		Date deadline = newest.addMonths(-1);
		while (true) {
			List<ArticleRef> newArticles = downloadArticleRefs(offset, observer);
			if (newArticles.isEmpty()) {
				log.info("No articles on page", offset);
				return ret;
			}
			for (ArticleRef ref : newArticles) {
				if (ref.getDate().isBefore(deadline)) {
					log.info("Deadline reached on page", offset, "->", deadline);
					return ret;
				}
				if (ret.contains(ref)) {
					log.info("Last page reached:", offset);
					return ret;
				}
				if (knownArticleIds.contains(ref.getPageId())) continue;
				ret.add(ref);
			}
			offset++;
		}
	}

	static List<ArticleRef> downloadArticleRefs(int indexOffset, OperationObserver observer) throws ParseException {
		if (indexOffset == 0) throw new IllegalArgumentException("page 0 does not exist");
		String url = URL_TEST_INDEX + "?fd=" + indexOffset;
		observer.onOperationInfoChanged(OperationObserver.DOWNLOADING, url);
		String data = IO.downloadUrlToString(url);

		ArrayList<ArticleRef> ret = new ArrayList<ArticleRef>();
		Parser parser = new Parser(data);
		parser.gotoAfter("<ul class=\"search-result-list\">");
		while (parser.gotoAfterIfNext("<li>")) {
			parser.gotoAfter("<a href=\"/");
			String pageRef = parser.getUntil("/\"");
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
			ArticleRef articleRef = new ArticleRef(date, title, pageRef);
			ret.add(articleRef);
		}

		return ret;
	}

	public static class Article {

		private ArticleRef ref;
		// private String imageUrl;
		// private String pdfUrl;
		// private String videoUrl;
		private List<SubArticleRef> subArticles;

		public Article(ArticleRef ref, List<SubArticleRef> subArticles) {
			super();
			this.ref = ref;
			this.subArticles = subArticles;
		}

		public List<SubArticleRef> getSubArticles() {
			return subArticles;
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
			return subArticles.size() + " in " + ref.toString();
		}

	}

	public static class SubArticleRef {

		private String title;
		private String pageRef;
		private String pageId;

		public SubArticleRef(String title, String pageRef) {
			super();
			this.title = title;
			this.pageRef = pageRef;

			pageId = pageRef;
			pageId = pageId.substring(pageId.lastIndexOf('-') + 1);
		}

		public String getTitle() {
			return title;
		}

		public String getPageRef() {
			return pageRef;
		}

		public String getPageId() {
			return pageId;
		}

		@Override
		public String toString() {
			return getTitle();
		}
	}

	public static class ArticlesIndex {

		private List<ArticleRef> articles = new ArrayList<ArticleRef>();

		public List<ArticleRef> getArticles() {
			return articles;
		}

		public void addNewArticles(List<ArticleRef> newArticles) {
			articles.addAll(0, newArticles);
		}

		public ArticleRef getLastKnownArticle() {
			if (articles.isEmpty()) return null;
			return articles.get(0);
		}

		@Override
		public String toString() {
			return articles.size() + " articles";
		}

		public Object getArticlesCount() {
			if (articles == null) return 0;
			return articles.size();
		}

	}

	public static class ArticleRef implements Comparable<ArticleRef> {

		private String title;
		private Date date;
		private String pageRef;
		private String pageId;

		private transient String titleMainPart;
		private transient String titleSubPart;

		public ArticleRef(Date date, String title, String pageRef) {
			super();
			this.date = date;
			this.title = title;
			this.pageRef = pageRef;

			pageId = pageRef;
			pageId = Str.removeSuffix(pageId, "-0");
			if (pageId.contains("-")) {
				pageId = pageId.substring(pageId.lastIndexOf('-') + 1);
			}
		}

		public ArticleRef() {}

		public String getUrl() {
			return URL_BASE + "/" + pageRef + "/";
		}

		public String getTitleMainPart() {
			if (titleMainPart == null) {
				int idx = title.indexOf(": ");
				if (idx < 0) return title;
				titleMainPart = title.substring(0, idx);
			}
			return titleMainPart;
		}

		public String getTitleSubPart() {
			if (titleSubPart == null) {
				int idx = title.indexOf(": ");
				if (idx < 0) return "";
				titleSubPart = title.substring(idx + 2);
			}
			return titleSubPart;
		}

		public Date getDate() {
			return date;
		}

		public String getTitle() {
			return title;
		}

		public String getPageRef() {
			return pageRef;
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
