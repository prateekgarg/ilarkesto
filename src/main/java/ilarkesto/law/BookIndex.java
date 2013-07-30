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

import ilarkesto.core.base.Lazy;
import ilarkesto.json.AJsonWrapper;
import ilarkesto.json.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BookIndex extends AJsonWrapper {

	public BookIndex(JsonObject json) {
		super(json);
	}

	public BookIndex(String code, String title) {
		json.put("code", code);
		json.put("title", title);
	}

	public BookRef getBookByCode(String code) {
		if (code == null) return null;
		return booksByCode.get().get(code.toLowerCase());
	}

	public Set<String> getBookCodes() {
		return booksByCode.get().keySet();
	}

	public String getCode() {
		return json.getString("code");
	}

	public String getTitle() {
		return json.getString("title");
	}

	public List<BookRef> getBooks() {
		return getWrapperArray("books", BookRef.class);
	}

	public void addBook(BookRef book) {
		json.addToArray("books", book);
	}

	@Override
	public String toString() {
		return getCode() + " " + getTitle();
	}

	@Override
	public boolean equals(Object obj) {
		return checkEquals(obj, "code");
	}

	@Override
	public int hashCode() {
		return getCode().hashCode();
	}

	private Lazy<Map<String, BookRef>> booksByCode = new Lazy<Map<String, BookRef>>() {

		@Override
		protected Map<String, BookRef> create() {
			Map<String, BookRef> ret = new HashMap<String, BookRef>();
			for (BookRef book : getBooks()) {
				ret.put(book.getCode().toLowerCase(), book);
			}
			return ret;
		}
	};

}
