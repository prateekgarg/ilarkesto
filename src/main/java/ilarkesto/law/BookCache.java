package ilarkesto.law;

import ilarkesto.json.ARemoteJsonCache;

import java.io.File;

public class BookCache extends ARemoteJsonCache<Book> {

	private ALawProvider lawProvider;
	private BookRef bookRef;

	BookCache(BookRef bookRef, File file, ALawProvider lawProvider) {
		super(Book.class, file);
		this.bookRef = bookRef;
		this.lawProvider = lawProvider;
	}

	@Override
	protected Book onUpdate(Book book, boolean forced, boolean invalidated) {
		if (!forced && !invalidated) {
			if (getDaysSinceLastUpdated() < 30) return null;
		}
		return lawProvider.loadBook(bookRef);
	}

	public String getSourceUrl() {
		return lawProvider.getSourceUrl(bookRef);
	}

}
