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
import ilarkesto.testng.ATest;

import java.io.File;

import org.testng.annotations.Test;

public class PdfBuilderTest extends ATest {

	@Test
	public void multiPageTable() {
		PdfBuilder pdf = new PdfBuilder();

		ATable table = pdf.table(1);

		table.headerRow().cell("HEADER");

		for (int i = 0; i < 100; i++) {
			ARow row = table.row();
			row.cell("row " + i);
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

}
