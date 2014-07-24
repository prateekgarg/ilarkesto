/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package ilarkesto.integration.law.rechtnrwde;

import ilarkesto.core.base.Parser;
import ilarkesto.core.base.Parser.ParseException;
import ilarkesto.core.html.Html;
import ilarkesto.io.AFileStorage;
import ilarkesto.io.IO;
import ilarkesto.law.ALawProvider;
import ilarkesto.law.Book;
import ilarkesto.law.BookIndex;
import ilarkesto.law.BookRef;
import ilarkesto.law.DataLoadFailedException;
import ilarkesto.net.HttpDownloader;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class RechtNrwDeLawProvider extends ALawProvider {

	public String BASE_URL = "https://recht.nrw.de/";

	private static final String charset = IO.ISO_LATIN_1;

	private HttpDownloader downloader;
	private AFileStorage dataStorage;

	public RechtNrwDeLawProvider(AFileStorage dataStorage) {
		this.dataStorage = dataStorage;
		this.downloader = new HttpDownloader().setBaseUrl(BASE_URL);
	}

	@Override
	public String getSourceUrl(String bookCode) {
		return BASE_URL; // TODO
	}

	@Override
	protected BookIndex loadBookIndex() {
		BookIndex index = new BookIndex("NRW", "Gesetze und Verordnungen NRW");
		for (int i = 1; i <= 9; i++) {
			loadGliederung(index, i);
		}
		return index;
	}

	private void loadGliederung(BookIndex index, int gldNr) throws DataLoadFailedException {
		String path = "lmi/owa/br_gliederung?gld_nr=" + gldNr;
		String data = downloader.downloadText(path, charset);
		// log.TEST(data);

		List<String> listPaths = new ArrayList<String>();

		Parser parser = new Parser(data);
		try {
			parser.gotoAfter("class=\"untergliederung\"");
			parser.gotoAfter("<table");
			parser.gotoAfter("</tr>"); // header row
			parser.skipWhitespace();
			while (parser.gotoAfterIfNext("<tr><th class=\"thsub\" scope=\"row\">")) {
				parser.gotoAfter("<td>");
				if (parser.isNext("<a")) {
					parser.gotoAfter("href=\"");
					String listPath = parser.getUntil("\"");
					listPaths.add(Html.convertHtmlToText(listPath));
				}
				parser.gotoAfter("</tr>");
				parser.skipWhitespace();
			}
		} catch (ParseException ex) {
			throw new DataLoadFailedException("Parsing 'Gliederung' failed: " + path, ex);
		}

		for (String listPath : listPaths) {
			loadUntergliederung(index, listPath);
		}
	}

	private void loadUntergliederung(BookIndex index, String path) {
		String data = downloader.downloadText("lmi/owa/" + path, charset);
		Parser parser = new Parser(data);

		try {
			parser.gotoAfter("<h1>Geltende Gesetze und Verordnungen");
			parser.gotoAfter("<table>");
			parser.skipWhitespace();
			while (parser.gotoAfterIfNext("<tr>")) {
				parser.gotoAfter("<a href=\"");
				parser.gotoAfter("bes_id=");
				int besId = Integer.parseInt(parser.getUntil("&"));
				parser.gotoAfter(">");
				String title = parser.getUntil("</a>");
				title = Html.convertHtmlToText(title);
				parser.gotoAfter("</tr>");
				parser.skipWhitespace();

				String code = createBookCode(besId, title);
				log.info(besId, code, title);
				BookRef book = new BookRef(code, title);
				book.getJson().put("rechtNrwBesId", besId);
				index.addBook(book);

			}
		} catch (ParseException ex) {
			throw new DataLoadFailedException("Parsing 'Untergliederung' failed: " + path, ex);
		}
	}

	private String createBookCode(int besId, String title) {
		StringBuilder sb = new StringBuilder("NRW_");
		StringTokenizer tokenizer = new StringTokenizer(title);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			char c = token.charAt(0);
			if (!Character.isLetter(c)) continue;
			sb.append(c);
		}
		if (besId == 21104) sb.append("2");
		return sb.toString();
	}

	@Override
	public BookIndex loadPrepackagedBookIndex() {
		return null;
	}

	@Override
	protected Book loadBook(BookRef bookRef) {
		return null;
	}

	@Override
	public AFileStorage getDataStorage() {
		return dataStorage;
	}

	@Override
	public String getSourceUrl() {
		return BASE_URL;
	}

}
