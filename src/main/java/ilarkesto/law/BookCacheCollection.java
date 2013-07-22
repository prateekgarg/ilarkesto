package ilarkesto.law;

import java.io.File;

public class BookCacheCollection {

	private ALawProvider lawProvider;
	private BookIndexCache bookIndexCache;

	public BookCacheCollection(ALawProvider lawProvider) {
		super();
		this.lawProvider = lawProvider;
	}

	public BookCache getBookCache(BookRef bookRef) {
		if (bookRef == null) return null;
		File file = new File(lawProvider.getDataDir().getPath() + "/" + bookRef.getCode() + ".json");
		return new BookCache(bookRef, file, lawProvider);
	}

	public synchronized BookIndexCache getBookIndexCache() {
		if (bookIndexCache == null) {
			File file = new File(lawProvider.getDataDir().getPath() + "/index.json");
			bookIndexCache = new BookIndexCache(file, lawProvider);
		}
		return bookIndexCache;
	}

	public synchronized void releaseCachedData() {
		bookIndexCache = null;
	}

}
