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
package ilarkesto.tools;

import ilarkesto.base.Str;
import ilarkesto.integration.kba.btkatowi.Tatbestandskatalog;
import ilarkesto.integration.kba.btkatowi.Tatbestandskatalog.Tatbestand;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

public class TatbestandskatalogParser {

	public static void main(String[] args) throws Exception {
		File file = new File("etc/bkat_owi_01042013_pdf.xml");
		new TatbestandskatalogParser(file);
	}

	private List<Tatbestand> tatbestaende = new LinkedList<Tatbestandskatalog.Tatbestand>();
	private int page;
	private boolean inTable;
	private TatbestandBuilder tb;
	int lineNr = 0;

	public TatbestandskatalogParser(File file) throws Exception {
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = in.readLine()) != null) {
			lineNr++;
			processLine(line);
		}
		in.close();
		if (tb != null) createTatbestand();

		Tatbestandskatalog tatbestandskatalog = new Tatbestandskatalog(tatbestaende);

		Tatbestand tb1 = tatbestandskatalog.getTatbestand(101000);
		if (tb1 == null) throw new RuntimeException("Tatbestand fehlt: 101000");
		if (!tb1.getFapPkt().equals("( B - 1 )")) throw new RuntimeException(tb1.toString());
		if (tb1.getEuro() != 3500) throw new RuntimeException(tb1.toString());
		if (tb1.getFv() != null) throw new RuntimeException(tb1.toString());

		Tatbestand tb2 = tatbestandskatalog.getTatbestand(103851);
		if (tb2 == null) throw new RuntimeException("Tatbestand fehlt: 103851");
		if (!tb2.getFapPkt().equals("A - 4")) throw new RuntimeException(tb2.toString());
		if (tb2.getEuro() != 44000) throw new RuntimeException(tb2.toString());
		if (tb2.getFv() != 2) throw new RuntimeException(tb2.toString());

		System.out.println(tatbestandskatalog.getJson().toFormatedString());
		File outputFile = new File("runtimedata/tatbestandskatalog.json").getAbsoluteFile();
		System.out.println(outputFile);
		tatbestandskatalog.getJson().write(outputFile, true);
	}

	private void processLine(String line) {
		if (line.startsWith("<page")) {
			page = Integer.parseInt(Str.cutFromTo(line, "number=\"", "\"")) + 4;
			return;
		}

		if (page < 23) return;

		if (!line.startsWith("<text")) return;

		int left = Integer.parseInt(Str.cutFromTo(line, "left=\"", "\""));
		int width = Integer.parseInt(Str.cutFromTo(line, "width=\"", "\""));
		int height = Integer.parseInt(Str.cutFromTo(line, "height=\"", "\""));
		int font = Integer.parseInt(Str.cutFromTo(line, "font=\"", "\""));
		String text = Str.cutFromTo(line, ">", "</text>");

		if (!inTable) {
			if (text.equals("FV")) inTable = true;
			return;
		}

		if (text.equals("<b>TBNR</b>") || text.startsWith("Stand: 1. April 2013")) {
			inTable = false;
			return;
		}

		if (height != 11) return;
		if (font != 1) return;
		if (text.trim().length() == 0) return;

		if (left == 106 && width == 40) {
			if (Character.isDigit(text.charAt(0))) {
				if (tb != null) createTatbestand();
				int nr = Integer.parseInt(text);
				tb = new TatbestandBuilder(nr);
				return;
			}
		}

		if (tb == null) return;

		if (left >= 765) {
			try {
				int fv = Integer.parseInt(Str.cutFromTo(line, ">", "M").trim());
				tb.setFv(fv);
			} catch (Exception ex) {
				System.out.println("!!!! " + line);
				throw new RuntimeException(lineNr + ":" + line, ex);
			}
			return;
		}

		if (left >= 708) {
			int euro = Integer.parseInt(Str.cutFromTo(line, ">", "<").replace(",", ""));
			tb.setEuro(euro);
			return;
		}

		if (left >= 163) {
			tb.appendFapPkt(text);
			return;
		}

		tb.appendText(text);

	}

	private void createTatbestand() {
		Tatbestand tatbestand = tb.createTatbestand();
		tatbestaende.add(tatbestand);
		System.out.println(tatbestand);
	}

	public static class TatbestandBuilder {

		private int nr;
		private String text;
		private String referencesText;
		String fapPkt;
		Integer euro;
		Integer fv;

		public TatbestandBuilder(int nr) {
			super();
			this.nr = nr;
		}

		public void appendFapPkt(String s) {
			if (fapPkt == null) {
				fapPkt = s;
				return;
			}
			fapPkt += " " + s;
		}

		public void appendText(String s) {
			if (referencesText != null || s.startsWith("§")) {
				appendReferencesText(s);
				return;
			}

			if (text == null) {
				text = s;
				return;
			}

			String separator = " ";
			if (text.endsWith("-")) {
				text = Str.removeSuffix(text, "-");
				separator = "";
			}

			if (s.endsWith(".")) separator = "\n";

			text += separator + s;
		}

		private void appendReferencesText(String s) {
			if (referencesText == null) {
				referencesText = s;
				return;
			}

			referencesText += "\n" + s;
		}

		public void setEuro(int euro) {
			this.euro = euro;
		}

		public void setFv(int fv) {
			this.fv = fv;
		}

		public Tatbestand createTatbestand() {
			return new Tatbestand(nr, text, referencesText, fapPkt, euro, fv);
		}

	}

}
