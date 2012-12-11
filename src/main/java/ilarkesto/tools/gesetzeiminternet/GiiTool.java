package ilarkesto.tools.gesetzeiminternet;

import ilarkesto.core.base.Parser;
import ilarkesto.core.base.Parser.ParseException;
import ilarkesto.io.IO;

public class GiiTool {

	public static void main(String[] args) throws ParseException {
		listBooks("A");
		listBooks("B");
		listBooks("C");
		listBooks("D");
		listBooks("E");
		listBooks("F");
		listBooks("G");
		listBooks("H");
		listBooks("I");
		listBooks("J");
		listBooks("K");
		listBooks("L");
		listBooks("M");
		listBooks("N");
		listBooks("O");
		listBooks("P");
		listBooks("Q");
		listBooks("R");
		listBooks("S");
		listBooks("T");
		listBooks("U");
		listBooks("V");
		listBooks("W");
		listBooks("Y");
		listBooks("Z");
		listBooks("1");
		listBooks("2");
		listBooks("3");
		listBooks("4");
		listBooks("5");
		listBooks("6");
		listBooks("7");
		listBooks("8");
		listBooks("9");
	}

	private static void listBooks(String teilliste) throws ParseException {
		String url = "http://www.gesetze-im-internet.de/Teilliste_" + teilliste + ".html";
		String data = IO.downloadUrlToString(url, IO.ISO_LATIN_1);
		Parser parser = new Parser(data);
		parser.gotoAfter("<div id=\"container\">");

		while (parser.gotoAfterIf("<p><a href=\"./")) {
			String reference = parser.getUntil("/index.html\"");
			parser.gotoAfter("<abbr title=\"");
			String title = parser.getUntil("\"");
			parser.gotoAfter("\">");
			String code = parser.getUntil("<").trim();
			System.out.println(reference + " " + code + " " + title);
		}
	}
}
