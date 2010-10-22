package ilarkesto.integration.soundunwound;

import ilarkesto.testng.ATest;

import org.testng.annotations.Test;

public class SoundunwoundTest extends ATest {

	@Test
	public void determineIdByTitle() {
		assertEquals(Soundunwound.determineIdByTitle("Ohrbooten - Babylon Bei Boot", false), "7360478");
	}

	@Test
	public void loadRecord() {
		SoundunwoundRecord r = Soundunwound.loadRecord("7360478");
		assertEquals(r.getArtist(), "Ohrbooten");
		assertEquals(r.getTitle(), "Babylon Bei Boot");
		assertEquals(r.getYear(), new Integer(2007));
		assertEquals(r.getCoverId(), "516z8jFoywL");
	}

}
