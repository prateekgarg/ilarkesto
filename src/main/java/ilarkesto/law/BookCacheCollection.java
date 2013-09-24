package ilarkesto.law;

import ilarkesto.core.base.Lazy;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class BookCacheCollection {

	private ALawProvider lawProvider;
	private Map<String, BookCache> bookCachesByCode = Collections.synchronizedMap(new WeakHashMap<String, BookCache>());

	public BookCacheCollection(ALawProvider lawProvider) {
		super();
		this.lawProvider = lawProvider;
	}

	public BookCache getBookCache(BookRef bookRef) {
		if (bookRef == null) return null;
		return getBookCache(bookRef.getCode());
	}

	public synchronized void releaseCaches() {
		for (BookCache bookCache : bookCachesByCode.values()) {
			bookCache.unload();
		}
		bookCachesByCode.clear();
		bookIndexCache.release();
	}

	public synchronized BookCache getBookCache(String bookCode) {
		if (bookCode == null) return null;

		BookCache bookCache = bookCachesByCode.get(bookCode);
		if (bookCache == null) {
			File file = lawProvider.getDataStorage().getFile(bookCode + ".json");
			bookCache = new BookCache(bookCode, file, bookIndexCache.get(), lawProvider);
			bookCachesByCode.put(bookCode, bookCache);
		}
		return bookCache;
	}

	public final Lazy<BookIndexCache> bookIndexCache = new Lazy<BookIndexCache>() {

		@Override
		protected BookIndexCache create() {
			File file = lawProvider.getDataStorage().getFile("index.json");
			return new BookIndexCache(file, lawProvider);
		}
	};

}
