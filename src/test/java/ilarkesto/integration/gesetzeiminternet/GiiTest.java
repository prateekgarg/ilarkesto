package ilarkesto.integration.gesetzeiminternet;

import ilarkesto.law.Book;
import ilarkesto.testng.ATest;

import java.util.List;

import org.testng.annotations.Test;

public class GiiTest extends ATest {

	@Test
	public void books() {
		GiiBookCollection collection = new GiiBookCollection();

		List<Book> books = collection.getBooks();
		assertNotEmpty(books);

		Book binSchStrOAbweichV = collection.getBookByCode("64. BinSchStrOAbweichV");
		assertNotNull(binSchStrOAbweichV);
		assertEquals(binSchStrOAbweichV.getTitle(),
			"Vierundsechzigste Verordnung zur vorübergehenden Abweichung von der Binnenschifffahrtsstraßen-Ordnung");
	}

}
