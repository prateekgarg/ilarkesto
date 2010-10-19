package ilarkesto.integration.soundunwound;

import ilarkesto.base.Str;
import ilarkesto.core.logging.Log;
import ilarkesto.integration.httpunit.HttpUnit;

import org.xml.sax.SAXException;

import com.meterware.httpunit.HTMLElement;
import com.meterware.httpunit.TableCell;
import com.meterware.httpunit.WebImage;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;

public class Soundunwound {

	private static Log log = Log.get(Soundunwound.class);

	public static String determineIdByTitle(String title, boolean guess) {
		log.info("Determining Soundunwound-ID by title:", title);
		WebResponse response = HttpUnit.loadPage(getTitleSearchUrl(title));
		WebTable table = HttpUnit.getTable("releases", response);
		if (table == null) return null;
		int rows = table.getRowCount();
		if (rows == 0) return null;
		if (rows > 1 && !guess) return null;
		TableCell cell = table.getTableCell(1, 2);
		if (cell == null) return null;
		for (WebLink link : cell.getLinks()) {
			String url = link.getURLString();
			if (url.startsWith("/music/-/") && url.endsWith("?ref=SR")) {
				String id = url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf("?"));
				return id;
			}
		}
		return null;
	}

	public static SoundunwoundRecord loadRecord(String soundunwoundId) {
		if (soundunwoundId == null) return null;
		String url = getPageUrl(soundunwoundId);
		log.info("Loading Soundunwound record:", soundunwoundId);
		WebResponse page = HttpUnit.loadPage(url);
		String title = null;
		String artist = null;
		Integer year;
		String coverId;
		try {
			String longTitle = parseLongTitle(page);
			if (longTitle != null) {
				int idx = longTitle.indexOf(" by ");
				if (idx < 0) {
					title = longTitle;
				} else {
					title = longTitle.substring(0, idx);
					artist = longTitle.substring(idx + 4);
				}
			}
			year = parseYear(page);
			coverId = parseCoverId(page);
		} catch (Exception ex) {
			throw new RuntimeException("Parsing Soundunwound page failed: " + url, ex);
		}

		return new SoundunwoundRecord(soundunwoundId, title, artist, year, coverId);
	}

	private static String parseLongTitle(WebResponse response) {
		String title = HttpUnit.getTitle(response);
		if (title == null) return null;
		return Str.cutTo(title, " - ");
	}

	private static Integer parseYear(WebResponse response) {
		// TODO
		String title;
		try {
			title = response.getTitle();
		} catch (SAXException ex) {
			throw new RuntimeException(ex);
		}
		if (title == null) return null;
		int idx = title.lastIndexOf(" (");
		if (idx < 1) return null;
		String year = title.substring(idx + 2, idx + 6);
		return Integer.parseInt(year);
	}

	private static String parseCoverId(WebResponse response) {
		// TODO
		HTMLElement img;
		try {
			img = response.getElementWithID("primary-poster");
		} catch (SAXException ex) {
			throw new RuntimeException(ex);
		}
		if (img == null) {
			TableCell td;
			try {
				td = (TableCell) response.getElementWithID("img_primary");
			} catch (SAXException ex1) {
				throw new RuntimeException(ex1);
			}
			WebImage[] images = td.getImages();
			if (images != null && images.length > 0) img = images[0];
		}

		if (img == null) return null;
		String url = img.getAttribute("src");
		if (url == null) return null;
		if (!url.startsWith("http://ia.media-imdb.com/images/M/")) return null;
		if (!url.contains("._")) return null;
		String id = Str.removePrefix(url, "http://ia.media-imdb.com/images/M/");
		id = id.substring(0, id.indexOf("._"));
		return id;
	}

	public static String getTitleSearchUrl(String title) {
		return "http://www.soundunwound.com/sp/search/find?searchPhrase=" + Str.encodeUrlParameter(title);
	}

	public static String getPageUrl(String id) {
		return "http://www.soundunwound.com/music/-/-/" + id;
	}

	public static String getCoverUrl(String coverId) {
		if (coverId == null) return null;
		return "http://ia.media-imdb.com/images/M/" + coverId + "._V1._SX510_SY755_.jpg";
	}

}
