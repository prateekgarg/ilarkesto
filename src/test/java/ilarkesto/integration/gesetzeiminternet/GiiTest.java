package ilarkesto.integration.gesetzeiminternet;

import ilarkesto.law.BookIndex;
import ilarkesto.law.BookIndexCache;
import ilarkesto.law.BookRef;
import ilarkesto.testng.ATest;

import org.testng.annotations.Test;

public class GiiTest extends ATest {

	@Test
	public void test() {
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

}
