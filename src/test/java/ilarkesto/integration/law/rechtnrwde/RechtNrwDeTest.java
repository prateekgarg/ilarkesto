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
package ilarkesto.integration.law.rechtnrwde;

import ilarkesto.core.base.SimpleFileStorage;
import ilarkesto.io.IO;
import ilarkesto.law.BookCacheCollection;
import ilarkesto.law.BookIndex;
import ilarkesto.law.BookIndexCache;
import ilarkesto.law.BookRef;
import ilarkesto.testng.ATest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.annotations.Test;

public class RechtNrwDeTest extends ATest {

	@Test
	public void index() {
		BookIndexCache indexCache = getBookCaches().bookIndexCache.get();
		indexCache.update(true);

		BookIndex index = indexCache.getPayload();
		List<BookRef> books = index.getBooks();
		assertNotEmpty(books);
		assertTrue(books.size() >= 1576);

		IO.writeFile(getTestOutputFile("RechtNrwBookIndex.json"), index.getJson().toFormatedString(), IO.UTF_8);

		// BookRef binSchStrOAbweichV = index.getBookByCode("64BinSchStrOAbweichV");
		// assertNotNull(binSchStrOAbweichV);
		// assertEquals(binSchStrOAbweichV.getTitle(),
		// "Vierundsechzigste Verordnung zur vorübergehenden Abweichung von der Binnenschifffahrtsstraßen-Ordnung");

		Set<String> codes = new HashSet<String>();
		for (BookRef book : books) {
			log.info(book);
			String code = book.getCode();
			// if (Str.containsNonLetterOrDigit(code)) fail("Illegal book code: " + code);
			if (codes.contains(code)) fail("Duplicate book code: " + code);
			codes.add(code);
		}
	}

	// --- ---

	private BookIndex getBookIndex() {
		BookIndexCache indexCache = getBookCaches().bookIndexCache.get();
		indexCache.update(false);

		BookIndex index = indexCache.getPayload();
		assertNotEmpty(index.getBooks());
		return index;
	}

	private BookCacheCollection getBookCaches() {
		return new BookCacheCollection(getGii());
	}

	private RechtNrwDeLawProvider getGii() {
		return new RechtNrwDeLawProvider(new SimpleFileStorage(getTestOutputFile("nrw-books")));
	}
}
