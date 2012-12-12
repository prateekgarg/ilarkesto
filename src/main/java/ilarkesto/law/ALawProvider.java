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
package ilarkesto.law;

import ilarkesto.core.base.RuntimeTracker;
import ilarkesto.core.logging.Log;

import java.util.HashMap;
import java.util.Map;

public abstract class ALawProvider {

	protected Log log = Log.get(getClass());

	private BookIndex bookIndex;
	private Map<BookRef, Book> books = new HashMap<BookRef, Book>();

	protected abstract BookIndex loadBookIndex();

	protected abstract Book loadBook(BookRef bookRef);

	public final BookIndex getBookIndex() {
		if (bookIndex == null) {
			log.info("Loading book index");
			RuntimeTracker rt = new RuntimeTracker();
			bookIndex = loadBookIndex();
			log.info("Index with", bookIndex.getBooks().size(), "books loaded in", rt);
		}
		return bookIndex;
	}

	public final Book getBook(BookRef bookRef) {
		Book book = books.get(bookRef);
		if (book == null) {
			log.info("Loading book:", bookRef);
			RuntimeTracker rt = new RuntimeTracker();
			book = loadBook(bookRef);
			log.info("Book with", book.getNorms().size(), "norms loaded loaded in", rt);
			books.put(bookRef, book);
		}
		return book;
	}

}
