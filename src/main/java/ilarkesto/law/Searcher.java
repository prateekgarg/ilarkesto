package ilarkesto.law;

import ilarkesto.core.logging.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class Searcher implements Runnable {

	private static Log log = Log.get(Searcher.class);

	private SearchResultConsumer consumer;
	private BookCacheCollection bookCaches;
	private List<String> searchStrings;

	private boolean broadenToCachedBooksEnabled;
	private boolean stopRequested;
	private BookRef restrictToBook;

	public Searcher(String query, SearchResultConsumer consumer, BookCacheCollection bookCaches) {
		super();
		this.searchStrings = parseQuery(query);
		this.consumer = consumer;
		this.bookCaches = bookCaches;
	}

	@Override
	public void run() {
		searchRestrictedBook();
		if (stopRequested) return;

		if (searchStrings.isEmpty()) return;

		if (restrictToBook != null) {
			log.info("Searching for norms in identified book:", restrictToBook, "->", searchStrings);
			consumer.onSearchRestricted(restrictToBook.getCode());
			searchForNorms(restrictToBook, true);
		} else {
			log.info("Searching for books:", searchStrings);
			searchForBooksWithoutNorms();
			if (stopRequested) return;
			if (broadenToCachedBooksEnabled) {
				consumer.onSearchBroadenedToCachedBooks();
				searchForNorms(bookCaches.bookIndexCache.get().getPayload().getBooks(), false);
			}
		}

		consumer.onSearchFinished();
		log.info("Search finished:", searchStrings);
	}

	private void searchRestrictedBook() {
		BookIndex index = bookCaches.bookIndexCache.get().getPayload();
		for (String code : searchStrings) {
			restrictToBook = index.getBookByCode(code);
			if (restrictToBook != null) {
				searchStrings.remove(code);
				consumer.onBookFound(restrictToBook);
				return;
			}
		}
	}

	private void searchForNorms(List<BookRef> bookRefs, boolean updateIfNull) {
		for (BookRef bookRef : bookRefs) {
			if (stopRequested) return;
			searchForNorms(bookRef, updateIfNull);
		}
	}

	private void searchForNorms(BookRef bookRef, boolean updateIfNull) {
		BookCache cache = bookCaches.getBookCache(bookRef);
		Book book = cache.getPayload();
		if (book == null && updateIfNull) {
			cache.update(false);
			book = cache.getPayload();
		}
		if (book == null) return;
		searchForNorms(book);
		cache.unload();
	}

	private void searchForNorms(Book book) {
		Set<String> alreadyFoundCodes = new HashSet<String>();
		List<Norm> norms = book.getAllNorms();
		for (Norm norm : norms) {
			if (stopRequested) return;
			if (searchStrings.size() == 1 && norm.getRef().isCodeNumber(searchStrings.get(0))) {
				consumer.onNormFound(norm);
				String code = norm.getRef().getCode();
				alreadyFoundCodes.add(code);
				continue;
			}
		}
		for (Norm norm : norms) {
			if (stopRequested) return;
			String code = norm.getRef().getCode();
			if (alreadyFoundCodes.contains(code)) continue;
			if (matchesSearch(code) || matchesSearch(norm.getTextAsString())) {
				consumer.onNormFound(norm);
				alreadyFoundCodes.add(code);
				continue;
			}
		}
	}

	private void searchForBooksWithoutNorms() {
		BookIndex index = bookCaches.bookIndexCache.get().getPayload();
		if (index == null) return;
		for (BookRef book : index.getBooks()) {
			if (stopRequested) return;
			checkBook(book);
		}
	}

	private void checkBook(BookRef book) {
		if (matchesSearch(book.getCode()) || matchesSearch(book.getTitle())) {
			consumer.onBookFound(book);
			return;
		}
	}

	private boolean matchesSearch(String test) {
		if (test == null) return false;
		if (searchStrings.isEmpty()) return false;
		test = test.toLowerCase();
		for (String s : searchStrings) {
			if (!test.contains(s)) return false;
		}
		return true;
	}

	public void setStopRequested(boolean stopRequested) {
		this.stopRequested = stopRequested;
	}

	private List<String> parseQuery(String searchString) {
		List<String> ret = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(searchString, " \t\n\r,.|§");
		while (tokenizer.hasMoreTokens()) {
			ret.add(tokenizer.nextToken().toLowerCase());
		}
		return ret;
	}

	public void setBroadenToCachedBooksEnabled(boolean broadenToCachedBooksEnabled) {
		this.broadenToCachedBooksEnabled = broadenToCachedBooksEnabled;
	}

	public static interface SearchResultConsumer {

		void onBookFound(BookRef book);

		void onSearchBroadenedToCachedBooks();

		void onSearchRestricted(String code);

		void onNormFound(Norm norm);

		void onSearchFinished();

	}

}
