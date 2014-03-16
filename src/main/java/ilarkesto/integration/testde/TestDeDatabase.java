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

import ilarkesto.core.auth.LoginData;
import ilarkesto.core.auth.LoginDataProvider;
import ilarkesto.core.base.AFileStorage;
import ilarkesto.core.base.OperationObserver;
import ilarkesto.core.base.Parser.ParseException;
import ilarkesto.core.base.SimpleFileStorage;
import ilarkesto.core.base.Str;
import ilarkesto.core.base.TextFileCache;
import ilarkesto.core.logging.Log;
import ilarkesto.core.time.Tm;
import ilarkesto.integration.testde.TestDe.Article;
import ilarkesto.integration.testde.TestDe.ArticleRef;
import ilarkesto.integration.testde.TestDe.ArticlesIndex;
import ilarkesto.integration.testde.TestDe.SubArticleRef;
import ilarkesto.io.IO;
import ilarkesto.json.JsonMapper;
import ilarkesto.json.JsonMapper.TypeResolver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestDeDatabase {

	public static void main(String[] args) throws ParseException {
		TestDeDatabase db = new TestDeDatabase(new SimpleFileStorage(new File("runtimedata/test.de")),
				LoginDataProvider.NULL_PROVIDER);
		db.updateIndex(OperationObserver.DUMMY);
	}

	private static Log log = Log.get(TestDeDatabase.class);

	private AFileStorage storage;

	private TextFileCache articlePagesCache;
	private ArticlesIndex index;
	private Class indexResourcePath;
	private Set<String> viewedArticles;
	private LoginDataProvider loginDataProvider;

	public TestDeDatabase(AFileStorage storage, LoginDataProvider loginDataProvider) {
		super();
		this.storage = storage;
		this.loginDataProvider = loginDataProvider;

		articlePagesCache = new TextFileCache(storage.getSubStorage("articlePagesCache"), new ArticlePageLoader());

		loadViewedArticles();
	}

	public boolean isViewed(ArticleRef articleRef) {
		return viewedArticles.contains(articleRef.getPageId());
	}

	public synchronized void markViewed(ArticleRef articleRef) {
		viewedArticles.add(articleRef.getPageId());
		saveViewedArticles();
	}

	public synchronized void markViewed(Collection<ArticleRef> articleRefs) {
		for (ArticleRef articleRef : articleRefs) {
			viewedArticles.add(articleRef.getPageId());
		}
		saveViewedArticles();
	}

	private synchronized void saveViewedArticles() {
		PrintWriter out;
		try {
			out = new PrintWriter(new FileWriter(getViewedArticlesFile()));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		for (String id : viewedArticles) {
			out.println(id);
		}
		IO.close(out);
	}

	private synchronized void loadViewedArticles() {
		viewedArticles = new HashSet<String>();
		File file = getViewedArticlesFile();
		if (file.exists()) {
			BufferedReader in;
			try {
				in = new BufferedReader(new FileReader(file));
			} catch (FileNotFoundException ex) {
				throw new RuntimeException(ex);
			}
			try {
				String line;
				while ((line = in.readLine()) != null) {
					String articleId = line.trim();
					if (Str.isBlank(articleId)) continue;
					viewedArticles.add(articleId);
				}
			} catch (IOException ex) {
				log.error(ex);
			} finally {
				IO.closeQuiet(in);
			}
		}
	}

	private File getViewedArticlesFile() {
		return storage.getFile("viewedArticles.txt");
	}

	public File loadPdf(SubArticleRef subArticleRef, ArticleRef articleRef, OperationObserver observer)
			throws ParseException {
		if (!subArticleRef.isPdf()) throw new IllegalArgumentException("Sub article is not PDF: " + subArticleRef);
		File pdfFile = getPdfFile(subArticleRef);
		if (pdfFile.exists()) return pdfFile;
		loginIfAvailable(observer);
		TestDe.downloadPdf(subArticleRef, articleRef, pdfFile, observer);
		return pdfFile;
	}

	private File getPdfFile(SubArticleRef ref) {
		String filename = Str.toFileCompatibleString(ref.getPageRef());
		return storage.getFile(filename);
	}

	public Article loadArticle(ArticleRef ref, OperationObserver observer) throws ParseException {
		String html = articlePagesCache.loadFromCache(ref.getPageRef(), observer);
		if (html == null) {
			loginIfAvailable(observer);
			html = articlePagesCache.load(ref.getPageRef(), observer);
		}
		return TestDe.parseArticle(ref, html);
	}

	private synchronized void loginIfAvailable(OperationObserver observer) {
		LoginData loginData = loginDataProvider.getLoginData();
		if (loginData == null) return;
		if (getLastLoginAgeInMinutes() < 10) return;
		login(loginData, observer);
	}

	private long getLastLoginAgeInMinutes() {
		return (System.currentTimeMillis() - lastLogin) / Tm.MINUTE;
	}

	private long lastLogin;

	private synchronized void login(LoginData loginData, OperationObserver observer) {
		lastLogin = 0;
		TestDe.login(loginData, observer);
		lastLogin = System.currentTimeMillis();
	}

	public void clearArticle(ArticleRef articleRef) {
		articlePagesCache.delete(articleRef.getPageRef());
	}

	public void clearSubArticle(SubArticleRef subArticleRef) {
		articlePagesCache.delete(subArticleRef.getPageRef());
	}

	public String loadSubArticleHtml(String pageRef, OperationObserver observer) {
		return articlePagesCache.load(pageRef, observer);
	}

	public String loadSubArticleHtml(SubArticleRef ref, OperationObserver observer) {
		loginIfAvailable(observer);
		return loadSubArticleHtml(ref.getPageRef(), observer);
	}

	private synchronized boolean importFromResource() {
		if (indexResourcePath == null) return false;
		File indexFile = getIndexFile();
		if (indexFile.exists()) return false;
		log.info("Importing index from resource");
		IO.copyResource("index.json", indexFile, indexResourcePath);
		return true;
	}

	public TestDeDatabase setIndexResourcePath(Class indexResourcePath) {
		this.indexResourcePath = indexResourcePath;
		return this;
	}

	public synchronized ArticlesIndex getIndex(OperationObserver observer) {
		if (index == null) {
			boolean imported = importFromResource();
			File indexFile = getIndexFile();
			observer.onOperationInfoChanged(OperationObserver.LOADING_CACHE, indexFile.getAbsolutePath());
			if (!indexFile.exists()) {
				log.info("Article index file does not exist:", indexFile);
				index = new ArticlesIndex();
				return index;
			}
			try {
				index = JsonMapper.deserialize(indexFile, ArticlesIndex.class, typeResolver);
				log.info("Article index loaded:", index.getArticlesCount(), "articles");
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
			if (imported) markViewed(index.getArticles());
		}
		return index;
	}

	// public void updateIndexIfNecessary(OperationObserver observer) {
	// File indexFile = getIndexFile();
	// TimePeriod age = new DateAndTime(indexFile.lastModified()).getPeriodToNow();
	// long ageInHours = age.toHours();
	// log.info("Last update was", ageInHours, "hours ago");
	// if (indexFile.exists() && ageInHours < 24) return;
	// updateIndex(observer);
	// }

	public synchronized List<ArticleRef> updateIndex(OperationObserver observer) {
		getIndex(observer);
		List<ArticleRef> newArticles;
		try {
			newArticles = TestDe.update(index, observer);
		} catch (ParseException ex1) {
			throw new RuntimeException(ex1);
		}
		File indexFile = getIndexFile();
		observer.onOperationInfoChanged(OperationObserver.SAVING, indexFile.getAbsolutePath());
		try {
			JsonMapper.serialize(index, indexFile);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		return newArticles;
	}

	public List<ArticleRef> getNewArticles(OperationObserver observer) {
		ArticlesIndex index = getIndex(observer);
		List<ArticleRef> ret = new ArrayList<ArticleRef>();
		for (ArticleRef ref : index.getArticles()) {
			if (isViewed(ref)) continue;
			ret.add(ref);
		}
		return ret;
	}

	public File getIndexFile() {
		return storage.getFile("index.json");
	}

	static final TypeResolver typeResolver = new TypeResolver() {

		@Override
		public Class resolveType(Object object, String field) {
			return null;
		}

		@Override
		public Class resolveArrayType(Object object, String field) {
			return ArticleRef.class;
		}
	};

}
