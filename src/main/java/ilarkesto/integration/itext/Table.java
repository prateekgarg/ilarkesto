/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>, Artjom Kochtchi
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.integration.itext;

import ilarkesto.core.base.Color;
import ilarkesto.core.base.Utl;
import ilarkesto.pdf.ACell;
import ilarkesto.pdf.APdfElement;
import ilarkesto.pdf.ARow;
import ilarkesto.pdf.ATable;
import ilarkesto.pdf.FontStyle;

import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class Table extends ATable implements ItextElement {

	private List<ARow> rows = new ArrayList<ARow>();

	public Table(APdfElement parent, FontStyle fontStyle) {
		super(parent, fontStyle);
	}

	@Override
	public Element getITextElement() {
		float[] cellWidths = getCellWidths();
		int columnCount = getColumnCount();
		PdfPTable t = cellWidths == null ? new PdfPTable(columnCount) : new PdfPTable(cellWidths);
		t.setHeaderRows(headerRowCount);
		t.setFooterRows(footerRowCount);

		t.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

		Float width = getWidth();
		if (width != null) t.setWidthPercentage(width);

		List<Integer> rowsToKeepTogether = new ArrayList<Integer>();

		int rowIdx = -1;
		for (ARow row : rows) {
			rowIdx++;
			for (ACell cell : row.getCells()) {
				t.addCell((PdfPCell) ((Cell) cell).getITextElement());
			}
			if (row.isKeepTogether()) rowsToKeepTogether.add(rowIdx);
		}

		if (!rowsToKeepTogether.isEmpty()) t.keepRowsTogether(Utl.toArrayOfInt(rowsToKeepTogether));

		return t;
	}

	@Override
	public ATable createCellBorders(Color color, float width) {
		float[] cellWidths = getCellWidths();
		int cols = cellWidths == null ? getColumnCount() : cellWidths.length;
		int col = 0;
		int rowIdx = 0;
		for (ARow row : rows) {
			for (ACell cell : row.getCells()) {
				if (rowIdx == 0) cell.setBorderTop(color, width);
				cell.setBorderRight(color, width);
				cell.setBorderBottom(color, width);
				if (col == 0) cell.setBorderLeft(color, width);
				col += cell.getColspan();
				if (col >= cols) {
					col = 0;
					rowIdx++;
				}
			}
		}
		return this;
	}

	@Override
	public ARow row() {
		ARow row = new ARow(this);
		rows.add(row);

		FontStyle defaultFontStyle = getFontStyle();
		if (defaultFontStyle != null) row.setDefaultFontStyle(defaultFontStyle);

		return row;
	}

	@Override
	public ARow row(Object... cellTexts) {
		ARow row = row();
		for (Object text : cellTexts)
			row.cell(text);
		return row;
	}

}
