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

import ilarkesto.core.base.AFileStorage;
import ilarkesto.core.base.OperationObserver;
import ilarkesto.core.base.Parser.ParseException;
import ilarkesto.core.base.SimpleFileStorage;
import ilarkesto.core.logging.Log;
import ilarkesto.core.time.Tm;
import ilarkesto.integration.testde.TestDe.ArticleRef;
import ilarkesto.integration.testde.TestDe.ArticlesIndex;
import ilarkesto.io.IO;
import ilarkesto.json.JsonMapper;
import ilarkesto.json.JsonMapper.TypeResolver;

import java.io.File;
import java.io.IOException;

public class TestDeDatabase {

	public static void main(String[] args) throws ParseException {
		TestDeDatabase db = new TestDeDatabase(new SimpleFileStorage(new File("runtimedata/test.de")));
		db.updateIndex(OperationObserver.DUMMY);
	}

	private static Log log = Log.get(TestDeDatabase.class);

	private AFileStorage storage;

	private ArticlesIndex index;

	private Class indexResourcePath;

	public TestDeDatabase(AFileStorage storage) {
		super();
		this.storage = storage;
	}

	private void importFromResource() {
		if (indexResourcePath == null) return;
		File indexFile = getIndexFile();
		if (indexFile.exists()) return;
		log.info("Importing index from resource");
		IO.copyResource("index.json", indexFile, indexResourcePath);
	}

	public TestDeDatabase setIndexResourcePath(Class indexResourcePath) {
		this.indexResourcePath = indexResourcePath;
		return this;
	}

	public ArticlesIndex getIndex(OperationObserver observer) {
		if (index == null) {
			importFromResource();
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
		}
		return index;
	}

	public void updateIndexIfNecessary(OperationObserver observer) {
		File indexFile = getIndexFile();
		if (indexFile.exists() && System.currentTimeMillis() - indexFile.lastModified() < Tm.DAY) return;
		updateIndex(observer);
	}

	public void updateIndex(OperationObserver observer) {
		getIndex(observer);
		boolean changed;
		try {
			changed = TestDe.update(index, observer);
		} catch (ParseException ex1) {
			throw new RuntimeException(ex1);
		}
		if (!changed) return;
		File indexFile = getIndexFile();
		observer.onOperationInfoChanged(OperationObserver.SAVING, indexFile.getAbsolutePath());
		try {
			JsonMapper.serialize(index, indexFile);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
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
