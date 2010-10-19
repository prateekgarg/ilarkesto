package ilarkesto.integration.soundunwound;

import ilarkesto.base.Str;
import ilarkesto.core.logging.Log;
import ilarkesto.integration.httpunit.HttpUnit;

import com.meterware.httpunit.TableCell;
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

	public static String getTitleSearchUrl(String title) {
		return "http://www.soundunwound.com/sp/search/find?searchPhrase=" + Str.encodeUrlParameter(title);
	}

	public static String getPageUrl(String id) {
		return "http://www.soundunwound.com/music/-/-/" + id;
	}

	public static String getCoverUrl(String soundunwoundId) {
		if (soundunwoundId == null) return null;
		return "http://ia.media-imdb.com/images/M/" + soundunwoundId + "._V1._SX510_SY755_.jpg";
	}

}
