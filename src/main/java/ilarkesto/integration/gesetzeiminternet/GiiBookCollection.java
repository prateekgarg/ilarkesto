package ilarkesto.integration.gesetzeiminternet;

import ilarkesto.core.base.Parser;
import ilarkesto.core.base.Parser.ParseException;
import ilarkesto.io.IO;
import ilarkesto.law.Book;
import ilarkesto.law.BookCollection;
import ilarkesto.law.DataLoadFailedException;
import ilarkesto.net.HttpDownloader;

import java.util.ArrayList;
import java.util.List;

public class GiiBookCollection extends BookCollection {

	private HttpDownloader downloader;

	public GiiBookCollection() {
		super("DE", "Deutsches Bundesrecht");
		this.downloader = new HttpDownloader().setCharset(IO.ISO_LATIN_1).setBaseUrl(
			"http://www.gesetze-im-internet.de/");
	}

	@Override
	protected List<Book> loadBooks() throws DataLoadFailedException {
		List<Book> books = new ArrayList<Book>();
		for (int i = 'A'; i <= 'Z'; i++) {
			if (i == 'X') continue;
			loadBooks(books, String.valueOf((char) i));
		}
		for (int i = 1; i <= 9; i++) {
			loadBooks(books, String.valueOf(i));
		}
		return books;
	}

	private void loadBooks(List<Book> books, String teilliste) throws DataLoadFailedException {
		String path = "Teilliste_" + teilliste + ".html";
		String data = downloader.downloadText(path);
		Parser parser = new Parser(data);

		try {
			parser.gotoAfter("<div id=\"container\">");
			while (parser.gotoAfterIf("<p><a href=\"./")) {
				String reference = parser.getUntil("/index.html\"");
				parser.gotoAfter("<abbr title=\"");
				String title = parser.getUntil("\"");
				parser.gotoAfter("\">");
				String code = parser.getUntil("<").trim();
				books.add(new GiiBook(reference, code, title));
			}
		} catch (ParseException ex) {
			throw new DataLoadFailedException("Parsing failed: " + path, ex);
		}
	}

	@Override
	protected String getInternetUrl() {
		return "http://www.gesetze-im-internet.de";
	}

}
