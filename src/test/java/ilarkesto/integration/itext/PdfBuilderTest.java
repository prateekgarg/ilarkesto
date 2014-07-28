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

import ilarkesto.io.IO;
import ilarkesto.pdf.ARow;
import ilarkesto.pdf.ATable;
import ilarkesto.testng.ATest;

import java.io.File;

import org.testng.annotations.Test;

public class PdfBuilderTest extends ATest {

	@Test
	public void test() {
		PdfBuilder pdf = new PdfBuilder();

		pdf.image(getTestImageFile());

		ATable table = pdf.table(1, 1);
		ARow row = table.row();
		row.cell().image(getTestImageFile());
		row.cell();

		File pdfFile = getTestOutputFile("test.pdf");
		log.info(pdfFile.getAbsolutePath());
		pdf.write(pdfFile);
	}

	private File getTestImageFile() {
		File file = getTestOutputFile("test.jpg");
		if (!file.exists()) IO.downloadUrlToFile("http://beust.com/pics/book-cover.jpg", file.getPath());
		return file;
	}

}
