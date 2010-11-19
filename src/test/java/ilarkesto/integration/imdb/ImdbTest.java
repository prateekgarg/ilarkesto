package ilarkesto.integration.imdb;

import ilarkesto.testng.ATest;

import org.testng.annotations.Test;

public class ImdbTest extends ATest {

	@Test
	public void loadRecord() {
		ImdbRecord killbill = Imdb.loadRecord("tt0266697");
		assertEquals(killbill.getTitle(), "Kill Bill: Vol. 1");
		assertEquals(killbill.getYear(), Integer.valueOf(2003));
		assertEquals(killbill.getCoverId(), "MV5BMTU1NDg1Mzg4M15BMl5BanBnXkFtZTYwMDExOTc3");

		ImdbRecord greek = Imdb.loadRecord("tt1226229");
		assertEquals(greek.getTitle(), "Get Him to the Greek");
		assertEquals(greek.getTitleDe(), "M&#xE4;nnertrip");
		assertEquals(greek.getYear(), Integer.valueOf(2010));
		assertEquals(greek.getCoverId(), "MV5BMjIyMzQ0MjExNV5BMl5BanBnXkFtZTcwMzkyMzgxMw@@");
	}

	@Test
	public void determineIdByTitleNoGuess() {
		assertEquals(Imdb.determineIdByTitle("Kill Bill: Vol. 1", false), "tt0266697");
	}

	@Test
	public void determineIdByTitleGuess() {
		assertEquals(Imdb.determineIdByTitle("Seven Samurai", true), "tt0047478");
	}

}
