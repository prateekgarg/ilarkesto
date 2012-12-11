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

import java.util.List;

public abstract class BookCollection {

	private Log log = Log.get(getClass());
	private List<Book> books;
	private String code;
	private String title;

	protected abstract List<Book> loadBooks() throws DataLoadFailedException;

	protected abstract String getInternetUrl();

	public BookCollection(String code, String title) {
		this.code = code;
		this.title = title;
	}

	public List<Book> getBooks() {
		if (books == null) {
			log.info("Loading books:", toString());
			RuntimeTracker rt = new RuntimeTracker();
			books = loadBooks();
			log.info(books.size(), "books loaded in", rt.getRuntimeFormated());
		}
		return books;
	}

	public Book getBookByCode(String code) {
		for (Book book : books) {
			if (code.equals(book.getCode())) return book;
		}
		return null;
	}

	public String getCode() {
		return code;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public String toString() {
		return getCode() + " " + getTitle();
	}

}
