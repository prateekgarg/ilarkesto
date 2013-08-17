package ilarkesto.law;

import ilarkesto.core.base.OperationObserver;
import ilarkesto.json.ARemoteJsonCache;

import java.io.File;

public class BookCache extends ARemoteJsonCache<Book> {

	private ALawProvider lawProvider;
	private String bookCode;
	private BookIndexCache bookIndexCache;

	BookCache(String bookCode, File file, BookIndexCache bookIndexCache, ALawProvider lawProvider) {
		super(Book.class, file);
		this.bookCode = bookCode;
		this.bookIndexCache = bookIndexCache;
		this.lawProvider = lawProvider;
	}

	@Override
	protected Book onUpdate(Book book, boolean forced, boolean invalidated, OperationObserver observer) {
		if (!forced && !invalidated) {
			if (getDaysSinceLastUpdated() < 30) return null;
		}

		BookIndex bookIndex = bookIndexCache.getPayload();
		if (bookIndex == null) {
			bookIndexCache.update(false, observer);
			bookIndex = bookIndexCache.getPayload();
		}

		BookRef bookRef = bookIndex.getBookByCode(bookCode);

		if (bookRef == null)
			throw new RuntimeException(bookCode + " not available at " + lawProvider.getSourceUrl(bookCode));

		observer.onOperationInfoChanged(OperationObserver.DOWNLOADING, lawProvider.getSourceUrl(bookCode));
		return lawProvider.getBook(bookRef);
	}

	public String getSourceUrl() {
		return lawProvider.getSourceUrl(bookCode);
	}

}
