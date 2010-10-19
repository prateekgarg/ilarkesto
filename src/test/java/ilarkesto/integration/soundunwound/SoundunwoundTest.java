package ilarkesto.integration.soundunwound;

import ilarkesto.testng.ATest;

public class SoundunwoundTest extends ATest {

	// @Test
	public void determineIdByTitle() {
		assertEquals(Soundunwound.determineIdByTitle("Ohrbooten - Babylon bei Boot", false), "7360478");
	}

}
