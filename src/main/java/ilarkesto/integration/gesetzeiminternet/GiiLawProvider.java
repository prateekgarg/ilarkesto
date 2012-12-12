package ilarkesto.integration.gesetzeiminternet;

import ilarkesto.core.base.Parser;
import ilarkesto.core.base.Parser.ParseException;
import ilarkesto.io.IO;
import ilarkesto.law.ALawProvider;
import ilarkesto.law.Book;
import ilarkesto.law.BookIndex;
import ilarkesto.law.BookRef;
import ilarkesto.law.DataLoadFailedException;
import ilarkesto.law.Norm;
import ilarkesto.net.HttpDownloader;

import java.util.List;

public class GiiLawProvider extends ALawProvider {

	public static void main(String[] args) {
		BookRef ref = new BookRef("AAppO", "AAppO...");
		ref.getJson().put("giiReference", "aappo");
		List<Norm> norms = new GiiLawProvider().getBook(ref).getNorms();
		for (Norm norm : norms) {
			System.out.println(norm);
		}
	}

	private HttpDownloader downloader;

	public GiiLawProvider() {
		this.downloader = new HttpDownloader().setCharset(IO.ISO_LATIN_1).setBaseUrl(
			"http://www.gesetze-im-internet.de/");
	}

	@Override
	protected Book loadBook(BookRef bookRef) {
		String reference = bookRef.getJson().getString("giiReference");
		if (reference == null)
			throw new DataLoadFailedException("Book reference has no giiReference property: " + bookRef.getJson(), null);
		String data = downloader.downloadZippedText(reference + "/xml.zip", IO.UTF_8);
		Book book = new Book(bookRef);
		try {
			new GiiBookXmlParser(data).parseInto(book);
		} catch (ParseException ex) {
			throw new RuntimeException("Parsing book failed: " + bookRef, ex);
		}
		return book;
	}

	@Override
	protected BookIndex loadBookIndex() {
		BookIndex index = new BookIndex("DE", "Deutsches Bundesrecht");
		for (int i = 'A'; i <= 'Z'; i++) {
			if (i == 'X') continue;
			loadBooks(index, String.valueOf((char) i));
		}
		for (int i = 1; i <= 9; i++) {
			loadBooks(index, String.valueOf(i));
		}
		return index;
	}

	private void loadBooks(BookIndex index, String teilliste) throws DataLoadFailedException {
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
				BookRef book = new BookRef(code, title);
				book.getJson().put("giiReference", reference);
				index.addBook(book);
			}
		} catch (ParseException ex) {
			throw new DataLoadFailedException("Parsing 'Teilliste' failed: " + path, ex);
		}
	}

}
