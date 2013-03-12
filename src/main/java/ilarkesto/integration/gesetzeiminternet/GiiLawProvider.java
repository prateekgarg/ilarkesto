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

import java.io.File;
import java.util.List;

public class GiiLawProvider extends ALawProvider {

	public static void main(String[] args) {
		BookRef ref = new BookRef("StVo", "Straßenverkehrs-Ordnung");
		ref.getJson().put("giiReference", "stvo");
		List<Norm> norms = new GiiLawProvider(new File("runtimedata/gii")).getBook(ref).getNorms();
		for (Norm norm : norms) {
			System.out.println("\n-------------------------------------");
			System.out.println(norm);
			System.out.println(norm.getTextAsHtml());
		}
	}

	private HttpDownloader downloader;
	private File dataDir;

	public GiiLawProvider(File dataDir) {
		this.dataDir = dataDir;
		this.downloader = new HttpDownloader().setCharset(IO.ISO_LATIN_1).setBaseUrl(
			"http://www.gesetze-im-internet.de/");
	}

	@Override
	protected Book loadBook(BookRef bookRef) {
		String reference = bookRef.getJson().getString("giiReference");
		if (reference == null)
			throw new DataLoadFailedException("Book reference has no giiReference property: " + bookRef.getJson(), null);

		File tempDir = getTempBookDataDir(bookRef.getCode());
		downloader.downloadZipAndExtract(reference + "/xml.zip", tempDir);
		String data = loadXmlFileFromDir(tempDir);

		Book book = new Book(bookRef);
		try {
			new GiiBookXmlParser(data).parseInto(book);
		} catch (ParseException ex) {
			throw new RuntimeException("Parsing book failed: " + bookRef, ex);
		}

		File dir = getBookDataDir(bookRef.getCode());
		IO.delete(dir);
		IO.move(tempDir, dir);

		return book;
	}

	private String loadXmlFileFromDir(File dir) {
		for (File file : dir.listFiles()) {
			if (file.getName().endsWith(".xml")) return IO.readFile(file, IO.UTF_8);
		}
		throw new RuntimeException("Missing XML file in downloaded ZIP: " + dir);
	}

	public File getBookDataDir(String bookCode) {
		return new File(dataDir.getPath() + "/" + bookCode);
	}

	private File getTempBookDataDir(String bookCode) {
		return new File(dataDir.getPath() + "/~" + bookCode);
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
