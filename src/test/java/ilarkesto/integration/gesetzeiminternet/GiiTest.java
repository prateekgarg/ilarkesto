package ilarkesto.integration.gesetzeiminternet;

import ilarkesto.law.BookIndex;
import ilarkesto.law.BookRef;
import ilarkesto.testng.ATest;

import org.testng.annotations.Test;

public class GiiTest extends ATest {

	@Test
	public void test() {
		GiiLawProvider gii = new GiiLawProvider();

		BookIndex index = gii.getBookIndex();
		assertNotEmpty(index.getBooks());

		BookRef binSchStrOAbweichV = index.getBookByCode("64. BinSchStrOAbweichV");
		assertNotNull(binSchStrOAbweichV);
		assertEquals(binSchStrOAbweichV.getTitle(),
			"Vierundsechzigste Verordnung zur vorübergehenden Abweichung von der Binnenschifffahrtsstraßen-Ordnung");
	}

}
