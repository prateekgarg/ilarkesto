package ilarkesto.integration.albumartorg;

import ilarkesto.integration.albumartorg.AlbumArt;
import ilarkesto.testng.ATest;

import org.testng.annotations.Test;

public class AlbumArtTest extends ATest {

	@Test
	public void determineCoverArtUrl() {
		assertEquals(AlbumArt.determineCoverUrl("Offspring - Smash"),
			"http://ecx.images-amazon.com/images/I/514ZXF0J70L.jpg");
	}

}
