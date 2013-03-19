package ilarkesto.law;

import java.io.File;

public class BookCacheCollection {

	private ALawProvider lawProvider;

	public BookCacheCollection(ALawProvider lawProvider) {
		super();
		this.lawProvider = lawProvider;
	}

	public BookCache getBookCache(BookRef bookRef) {
		if (bookRef == null) return null;
		File file = new File(lawProvider.getDataDir().getPath() + "/" + bookRef.getCode() + ".json");
		return new BookCache(bookRef, file, lawProvider);
	}

	public BookIndexCache getBookIndexCache() {
		File file = new File(lawProvider.getDataDir().getPath() + "/index.json");
		return new BookIndexCache(file, lawProvider);
	}

}
