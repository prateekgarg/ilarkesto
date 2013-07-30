package ilarkesto.law;

import ilarkesto.core.base.Lazy;

import java.io.File;

public class BookCacheCollection {

	private ALawProvider lawProvider;

	public BookCacheCollection(ALawProvider lawProvider) {
		super();
		this.lawProvider = lawProvider;
	}

	public BookCache getBookCache(BookRef bookRef) {
		if (bookRef == null) return null;
		return getBookCache(bookRef.getCode());
	}

	public BookCache getBookCache(String bookCode) {
		if (bookCode == null) return null;
		File file = lawProvider.getDataStorage().getFile(bookCode + ".json");
		return new BookCache(bookCode, file, bookIndexCache.get(), lawProvider);
	}

	public final Lazy<BookIndexCache> bookIndexCache = new Lazy<BookIndexCache>() {

		@Override
		protected BookIndexCache create() {
			File file = lawProvider.getDataStorage().getFile("index.json");
			return new BookIndexCache(file, lawProvider);
		}
	};

	public synchronized void releaseCachedData() {
		bookIndexCache.release();
	}

}
