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
package ilarkesto.integration.law.gesetzeiminternetde;

import ilarkesto.core.base.Parser;
import ilarkesto.core.base.Str;
import ilarkesto.core.html.Html;
import ilarkesto.core.logging.Log;
import ilarkesto.core.time.Date;
import ilarkesto.law.Book;
import ilarkesto.law.Norm;
import ilarkesto.law.NormRef;
import ilarkesto.law.Section;

public class GiiBookXmlParser extends Parser {

	private Book book;
	private Section section;
	private int currentDepth;

	public GiiBookXmlParser(String data) {
		super(data);
	}

	public void parseInto(Book book) throws ParseException {
		this.book = book;

		gotoAfter("<norm");

		gotoAfter("<ausfertigung-datum");
		gotoAfter(">");
		String ausfertigungsdatum = getUntil("</ausfertigung-datum>");
		book.setIssueDate(new Date(ausfertigungsdatum));

		if (gotoAfterIf("<standkommentar>")) {
			String standkommentar = getUntil("</standkommentar>");
			book.setStatusComment(Html.convertHtmlToText(standkommentar));
		}

		if (isBefore("<fussnoten>", "</norm")) {
			gotoAfter("<fussnoten>");
			gotoAfter("<Content>");
			String fussnoten = getUntil("</Content>");
			book.setFooterAsHtml(xmlToHtml(fussnoten));
		}

		gotoAfter("</norm");

		this.section = null;
		this.currentDepth = 0;
		while (gotoAfterIf("<norm")) {
			String s = getUntil("</norm>");
			new NormParser(s).parseNorm();
		}
	}

	private class NormParser extends Parser {

		public NormParser(String data) {
			super(data);
		}

		private void parseNorm() throws ParseException {
			gotoAfter("<metadaten>");
			if (isBefore("<gliederungseinheit>", "<textdaten>")) {
				gotoAfter("<gliederungskennzahl>");
				String gliederungskennzahl = getUntilAndGotoAfter("</gliederungskennzahl>");
				int depth = gliederungskennzahl.length() / 3;
				gotoAfter("<gliederungsbez>");
				String gliederungsbez = getUntilAndGotoAfter("</gliederungsbez>");
				gotoAfter("</gliederungseinheit>");
				Section s = new Section(gliederungsbez);
				Log.TEST("Gliederung:", depth, gliederungskennzahl, gliederungsbez);
				if (depth > currentDepth) {
					if (section == null) {
						book.addSection(s);
					} else {
						section.addSection(s);
					}
					currentDepth++;
				} else if (depth == currentDepth) {
					Section parentSection = section.getParentSection();
					if (parentSection == null) {
						section.getBook().addSection(s);
					} else {
						parentSection.addSection(s);
					}
				} else if (depth < currentDepth) {
					while (depth < currentDepth) {
						section = section.getParentSection();
						currentDepth--;
					}
					Section parentSection = section.getParentSection();
					if (parentSection == null) {
						section.getBook().addSection(s);
					} else {
						parentSection.addSection(s);
					}
				}
				section = s;
				if (!isNext("<enbez>")) return;
			}
			gotoAfter("<enbez>");
			String enbez = getUntilAndGotoAfter("</enbez>");
			enbez = Str.removePrefix(enbez, "(XXXX) ");
			String titel = null;
			if (isBefore("<titel", "</metadaten>")) {
				gotoAfter("<titel");
				gotoAfter(">");
				titel = getUntil("</titel");
				titel = titel.replace("\n<BR/>\n", "\n");
				titel = titel.replace("<BR/>\n", "\n");
				titel = titel.replace("<BR/>", "\n");
			}
			NormRef ref = new NormRef(book.getRef().getCode(), enbez);
			Norm norm = new Norm(ref, titel);

			if (gotoAfterIf("<textdaten>")) {
				if (gotoAfterIf("<text")) {
					gotoAfter(">");
					if (gotoAfterIf("<Content>")) {
						String content = getUntil("</Content>");
						content = xmlToHtml(content);
						norm.setTextAsHtml(content);

						if (section == null) {
							book.addNorm(norm);
						} else {
							section.addNorm(norm);
						}
					}
				}
			} else {
				Log.TEST("No <textdaten>:", ref);
			}
		}
	}

	private String xmlToHtml(String s) {
		if (s == null) return null;
		s = s.replace("<row", "<tr");
		s = s.replace("</row>", "</tr>");

		s = s.replace("<entry", "<td");
		s = s.replace("</entry>", "</td>");

		s = s.replace("nameend=\"col2\"", "colspan=\"2\"");
		s = s.replace("nameend=\"col3\"", "colspan=\"3\"");
		s = s.replace("nameend=\"col4\"", "colspan=\"4\"");

		s = s.replace(" Type=\"TIF\"", "");
		s = s.replace(" Units=\"", "width=\"");
		s = s.replace(" xml:space=\"preserve\"", "");

		return s;
	}
}
