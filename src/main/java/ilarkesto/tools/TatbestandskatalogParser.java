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
import ilarkesto.core.logging.Log;
import ilarkesto.integration.jdom.JDom;
import ilarkesto.integration.kba.btkatowi.Tatbestandskatalog;
import ilarkesto.integration.kba.btkatowi.Tatbestandskatalog.Tatbestand;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;

public class TatbestandskatalogParser {

	public static void main(String[] args) throws Exception {
		Log.setDebugEnabled(true);
		File file = new File("etc/bkat_owi_01052014_pdf.xml");
		new TatbestandskatalogParser(file);
	}

	public TatbestandskatalogParser(File file) throws Exception {
		Document document = JDom.createDocument(file);
		Element eRoot = document.getRootElement();

		List<Element> ePages = JDom.getChildren(eRoot, "page");
		System.out.println(ePages.size() + " pages");

		// processPage(ePages.get(54 - 1));
		// processPage(ePages.get(19 - 1));
		// if (true) return;

		List<Tatbestand> tatbestaende = new ArrayList<Tatbestandskatalog.Tatbestand>();
		for (int i = 19; i <= 474; i++) {
			tatbestaende.addAll(processPage(ePages.get(i - 1)));
		}

		Tatbestandskatalog tatbestandskatalog = new Tatbestandskatalog(tatbestaende);

		Tatbestand tb1 = tatbestandskatalog.getTatbestand(101000);
		if (tb1 == null) throw new RuntimeException("Tatbestand fehlt: 101000");
		if (!tb1.getFapPkt().equals("(B - 0)")) throw new RuntimeException(tb1.toString());
		if (tb1.getEuro() != 3500) throw new RuntimeException(tb1.toString());
		if (tb1.getFv() != null) throw new RuntimeException(tb1.toString());

		Tatbestand tb2 = tatbestandskatalog.getTatbestand(103851);
		if (tb2 == null) throw new RuntimeException("Tatbestand fehlt: 103851");
		if (!tb2.getFapPkt().equals("A - 2")) throw new RuntimeException(tb2.toString());
		if (tb2.getEuro() != 44000) throw new RuntimeException(tb2.toString());
		if (tb2.getFv() != 2) throw new RuntimeException(tb2.toString());

		System.out.println(tatbestandskatalog.getJson().toFormatedString());
		File outputFile = new File("runtimedata/tatbestandskatalog.json").getAbsoluteFile();
		System.out.println(outputFile);
		tatbestandskatalog.getJson().write(outputFile, true);
	}

	private List<Tatbestand> processPage(Element ePage) {
		String pageNumber = ePage.getAttributeValue("number");
		if (pageNumber.equals("443")) return Collections.emptyList();
		System.out.println("page " + pageNumber);
		List<Element> eTexts = JDom.getChildren(ePage, "text");

		List<Tatbestand> ret = new ArrayList<Tatbestandskatalog.Tatbestand>();

		TatbestandBuilder tb = null;

		for (Element eText : eTexts) {
			int top = Integer.parseInt(eText.getAttributeValue("top"));
			int left = Integer.parseInt(eText.getAttributeValue("left"));
			int width = Integer.parseInt(eText.getAttributeValue("width"));
			int height = Integer.parseInt(eText.getAttributeValue("height"));
			int font = Integer.parseInt(eText.getAttributeValue("font"));
			String text = eText.getText();

			if (top < 163) continue; // header
			if (top >= 939) continue; // footer

			if (tb != null) {
				if ((left == 156 && width == 23 && height == 11 && font == 1)
						|| (left == 106 && width == 4 && height == 15 && font == 18)) {
					// TB separator
					// System.out.println("        separator: " + text);
					ret.add(tb.createTatbestand());
					tb = null;
					continue;
				}
			}

			if ((left >= 627 && left <= 638) && height == 11 && font == 1) {
				// fap pkt
				// System.out.println("        fap pkt: " + text);
				tb.appendFapPkt(text.trim());
				continue;
			}

			if ((left >= 701 && left <= 744) && height == 11 && font == 1) {
				// eur
				// System.out.println("        eur: " + text);
				int euro = Integer.parseInt(text.trim().replace(",", ""));
				tb.setEuro(euro);
				continue;
			}

			if (left == 765 && height == 11 && font == 1) {
				// fv
				// System.out.println("        fv: " + text);
				int fv = Integer.parseInt(text.substring(0, text.indexOf(' ')));
				tb.setFv(fv);
				continue;
			}

			if ((left >= 106 && left <= 373) && height == 11 && font == 1) {
				// text column
				// System.out.println("        text: " + text);
				if (tb == null) {
					text = text.trim();
					if (text.isEmpty()) continue;
					int idx = text.indexOf(' ');
					int tbnr = Integer.parseInt(text.substring(0, idx));
					text = text.substring(idx);
					tb = new TatbestandBuilder(tbnr);
					tb.appendText(text.trim());
					continue;
				}

				tb.appendText(text.trim());
				continue;
			}

			if (text.trim().isEmpty()) continue;

			throw new RuntimeException("Unprocessed text element -> left=" + left + " text=" + text);

		}

		return ret;
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
			s = s.replaceAll("  ", " ");
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
			Tatbestand tatbestand = new Tatbestand(nr, text, referencesText, fapPkt, euro, fv);
			System.out.println("  " + tatbestand);
			return tatbestand;
		}

	}

}
