package ilarkesto.integration.gesetzeiminternet;

import ilarkesto.law.Book;
import ilarkesto.law.BookIndex;
import ilarkesto.law.BookIndexCache;
import ilarkesto.law.BookRef;
import ilarkesto.law.Norm;
import ilarkesto.testng.ATest;

import java.util.List;

import org.testng.annotations.Test;

public class GiiTest extends ATest {

	@Test
	public void testIndex() {
		GiiLawProvider gii = new GiiLawProvider(getTestOutputFile("books"));
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
	public void testStvg() {
		GiiLawProvider gii = new GiiLawProvider(getTestOutputFile("books"));
		BookIndexCache indexCache = new BookIndexCache(getTestOutputFile("index.json"), gii);
		indexCache.update(false);

		BookIndex index = indexCache.getPayload();
		assertNotEmpty(index.getBooks());

		BookRef ref = index.getBookByCode("StVG");
		Book book = gii.loadBook(ref);
		assertEquals(book.getRef().getCode(), "StVG");
		assertEquals(book.getRef().getTitle(), "Straßenverkehrsgesetz");

		List<Norm> norms = book.getNorms();
		assertSize(norms, 99);

		Norm n1 = norms.get(0);
		assertStartsWith(n1.getTextAsString(), "(1) Kraftfahrzeuge ");
		assertContains(n1.getTextAsString(), "(2) Als Kraftfahrzeuge im Sinne ");
	}

	@Test
	public void testBgb() {
		GiiLawProvider gii = new GiiLawProvider(getTestOutputFile("books"));
		BookIndexCache indexCache = new BookIndexCache(getTestOutputFile("index.json"), gii);
		indexCache.update(false);

		BookIndex index = indexCache.getPayload();
		assertNotEmpty(index.getBooks());

		BookRef ref = index.getBookByCode("BGB");
		Book book = gii.loadBook(ref);
		log.debug(book.getJson().toFormatedString());
		assertEquals(book.getRef().getCode(), "BGB");
		assertEquals(book.getRef().getTitle(), "Bürgerliches Gesetzbuch");
	}

	@Test
	public void testAabg() {
		GiiLawProvider gii = new GiiLawProvider(getTestOutputFile("books"));
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
