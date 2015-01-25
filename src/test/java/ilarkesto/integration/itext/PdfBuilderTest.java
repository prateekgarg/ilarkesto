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
package ilarkesto.integration.itext;

import ilarkesto.core.base.Color;
import ilarkesto.io.IO;
import ilarkesto.pdf.APageExtension;
import ilarkesto.pdf.APageLayer;
import ilarkesto.pdf.APdfContainerElement;
import ilarkesto.pdf.ARow;
import ilarkesto.pdf.ATable;
import ilarkesto.pdf.FontStyle;
import ilarkesto.testng.ATest;

import java.io.File;

import org.testng.annotations.Test;

public class PdfBuilderTest extends ATest {

	@Test
	public void html() {
		PdfBuilder pdf = new PdfBuilder();
		pdf.setFontStyle(new FontStyle().setSize(5).setFont("Times"));
		pdf.html("<b>bold</b><div><i>italic</i></div><div><u>unterline</u></div><div><strike>strike</strike></div><div><font color=\"#ff0000\">red</font></div><div>normal</div><div><table><tr><td>a</td><td>b</td></tr></table></div>");

		File pdfFile = getTestOutputFile("html.pdf");
		log.info(pdfFile.getAbsolutePath());
		pdf.write(pdfFile);
	}

	@Test
	public void tableRowKeepTogether() {
		PdfBuilder pdf = new PdfBuilder();

		ATable table = pdf.table(1);

		for (int i = 1; i < 50; i++) {
			ARow row = table.row();
			boolean keepTogether = i >= 30;
			row.setKeepTogether(keepTogether);
			row.cell().text("row " + i + " " + keepTogether);
		}

		File pdfFile = getTestOutputFile("tableRowKeepTogether.pdf");
		log.info(pdfFile.getAbsolutePath());
		pdf.write(pdfFile);
	}

	@Test
	public void multiPageTable() {
		PdfBuilder pdf = new PdfBuilder();

		ATable table = pdf.table(1);

		table.headerRow().cell("HEADER");

		FontStyle red = new FontStyle().setColor(Color.RED);
		FontStyle green = new FontStyle().setColor(Color.GREEN);
		for (int i = 0; i < 100; i++) {
			ARow row = table.row();
			row.cell().setFontStyle(i % 2 == 0 ? red : green).text("row " + i);
		}

		File pdfFile = getTestOutputFile("multiPageTable.pdf");
		log.info(pdfFile.getAbsolutePath());
		pdf.write(pdfFile);
	}

	@Test
	public void measurements() {
		PdfBuilder pdf = new PdfBuilder();

		pdf.setPageSizeToA4Landscape();
		float margin = 20f;
		pdf.setMargins(margin, margin, margin, margin);

		table(pdf, "A4 Landscape | Margins: " + margin + " mm");

		pdf.addPageExtension(new APageExtension() {

			private float pos = 50f;

			@Override
			public void onPage(APageLayer page) {
				table(page, pos + "," + pos + " | " + pos + "x" + pos);
			}

			@Override
			public float getX(APageLayer page) {
				return pos;
			}

			@Override
			public float getY(APageLayer page) {
				return pos;
			}

			@Override
			public float getWidth(APageLayer page) {
				return pos;
			}

			@Override
			public float getHeight(APageLayer page) {
				return pos;
			}

		});

		File pdfFile = getTestOutputFile("measurements.pdf");
		log.info(pdfFile.getAbsolutePath());
		pdf.write(pdfFile);
	}

	@Test
	public void extensions() {
		PdfBuilder pdf = new PdfBuilder();

		for (int i = 0; i < 100; i++) {
			pdf.text("Paragraph " + i);
		}

		pdf.addPageExtension(new APageExtension() {

			@Override
			public void onPage(APageLayer page) {
				page.text("header");
			}
		});

		pdf.addPageExtension(new APageExtension() {

			@Override
			public void onPage(APageLayer page) {
				page.text("footer - page " + page.getPageNumber());
			}

			@Override
			public float getY(APageLayer page) {
				return page.getHeight() - 30f;
			}
		});

		File pdfFile = getTestOutputFile("extensions.pdf");
		log.info(pdfFile.getAbsolutePath());
		pdf.write(pdfFile);
	}

	@Test
	public void table() {
		PdfBuilder pdf = new PdfBuilder();

		pdf.image(getTestImageFile());

		ATable table = pdf.table(1, 1);
		ARow row = table.row();
		row.cell().image(getTestImageFile()).setScaleByHeight(30f);
		row.cell().image(getTestImageFile()).setScaleByHeight(10f);

		File pdfFile = getTestOutputFile("table.pdf");
		log.info(pdfFile.getAbsolutePath());
		pdf.write(pdfFile);
	}

	private File getTestImageFile() {
		File file = getTestOutputFile("test.jpg");
		if (!file.exists()) IO.downloadUrlToFile("http://beust.com/pics/book-cover.jpg", file.getPath());
		return file;
	}

	private void table(APdfContainerElement container, String text) {
		ATable table = container.table(1);
		// table.setWidth(50f);
		table.row().cell(text);
		table.createCellBorders(Color.BLUE, 0.2f);
	}

	@Test
	private void nestedTable() {
		PdfBuilder pdf = new PdfBuilder();

		ATable table = pdf.table(3, 8, 3);
		ARow row = table.row();
		row.cell("1").setBackgroundColor(Color.LIGHT_GRAY);
		row.cell("2").setBackgroundColor(Color.CYAN);
		row.cell("3").setBackgroundColor(Color.LIGHT_GRAY);

		row = table.row();
		row.cell("4").setBackgroundColor(Color.CYAN);
		ATable innerTable = row.cell().setBackgroundColor(Color.LIGHT_GRAY).table(2);
		ARow innerRow = innerTable.row();
		innerRow.cell("5A");
		innerRow.cell("5B");
		innerRow = innerTable.row();
		innerRow.cell("5C");
		innerRow.cell("5D");
		row.cell("6").setBackgroundColor(Color.CYAN);

		row = table.row();
		row.cell("7").setBackgroundColor(Color.LIGHT_GRAY);
		row.cell("8").setBackgroundColor(Color.CYAN);
		row.cell("9").setBackgroundColor(Color.LIGHT_GRAY);

		File pdfFile = getTestOutputFile("nestedtable.pdf");
		log.info(pdfFile.getAbsolutePath());
		pdf.write(pdfFile);
	}

	@Test
	private void colspan() {
		PdfBuilder pdf = new PdfBuilder();

		ATable table = pdf.table(1, 1);
		table.row().cell().setColspan(2).text("spanner!");
		ARow row = table.row();
		row.cell().text("no spanner1");
		row.cell().text("no spanner2");
		table.row().cell().setColspan(2).text("look! i'm spanning!");

		File pdfFile = getTestOutputFile("table-colspan.pdf");
		log.info(pdfFile.getAbsolutePath());
		pdf.write(pdfFile);
	}
}
