package ilarkesto.integration.gesetzeiminternet;

import ilarkesto.law.Book;
import ilarkesto.law.BookIndex;
import ilarkesto.law.BookIndexCache;
import ilarkesto.law.BookRef;
import ilarkesto.testng.ATest;

import org.testng.annotations.Test;

public class GiiTest extends ATest {

	@Test
	public void testIndex() {
		GiiLawProvider gii = new GiiLawProvider();
		BookIndexCache indexCache = new BookIndexCache(getTestOutputFile("index.json"), gii);
		indexCache.update(true);

		BookIndex index = indexCache.getPayload();
		assertNotEmpty(index.getBooks());

		BookRef binSchStrOAbweichV = index.getBookByCode("64. BinSchStrOAbweichV");
		assertNotNull(binSchStrOAbweichV);
		assertEquals(binSchStrOAbweichV.getTitle(),
			"Vierundsechzigste Verordnung zur vorübergehenden Abweichung von der Binnenschifffahrtsstraßen-Ordnung");

	}

	@Test
	public void testBgb() {
		GiiLawProvider gii = new GiiLawProvider();
		BookIndexCache indexCache = new BookIndexCache(getTestOutputFile("index.json"), gii);
		indexCache.update(false);

		BookIndex index = indexCache.getPayload();
		assertNotEmpty(index.getBooks());

		BookRef ref = index.getBookByCode("BGB");
		Book book = gii.loadBook(ref);
		assertEquals(book.getRef().getCode(), "BGB");
		assertEquals(book.getRef().getTitle(), "Bürgerliches Gesetzbuch");
	}

	@Test
	public void testAabg() {
		GiiLawProvider gii = new GiiLawProvider();
		BookIndexCache indexCache = new BookIndexCache(getTestOutputFile("index.json"), gii);
		indexCache.update(false);

		BookIndex index = indexCache.getPayload();
		assertNotEmpty(index.getBooks());

		BookRef ref = index.getBookByCode("AABG");
		Book book = gii.loadBook(ref);
		assertEquals(book.getRef().getCode(), "AABG");
		assertEquals(book.getRef().getTitle(),
			"Gesetz zur Begrenzung der Arzneimittelausgaben der gesetzlichen Krankenversicherung");

		assertSize(book.getNorms(), 4);
	}

}
