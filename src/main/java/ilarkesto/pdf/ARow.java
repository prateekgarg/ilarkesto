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
package ilarkesto.pdf;

import ilarkesto.core.base.Color;
import ilarkesto.integration.itext.Cell;

import java.util.ArrayList;
import java.util.List;

public class ARow {

	private List<ACell> cells = new ArrayList<ACell>();
	private Color defaultBackgroundColor;
	private FontStyle defaultFontStyle;
	private boolean keepTogether;

	public ACell cell() {
		Cell c = new Cell(table, defaultFontStyle);
		cells.add(c);
		c.setBackgroundColor(defaultBackgroundColor);
		c.setFontStyle(defaultFontStyle);

		Float defaultCellPadding = table.getDefaultCellPadding();
		if (defaultCellPadding != null) c.setPadding(defaultCellPadding);

		return c;
	}

	public ACell cell(Object text, FontStyle fontStyle) {
		ACell cell = cell();
		cell.setFontStyle(fontStyle);
		cell.text(text);
		return cell;
	}

	public ACell cell(Object text) {
		ACell cell = cell();
		cell.text(text);
		return cell;
	}

	public ARow setKeepTogether(boolean keepTogether) {
		this.keepTogether = keepTogether;
		return this;
	}

	public boolean isKeepTogether() {
		return keepTogether;
	}

	public ARow setDefaultBackgroundColor(Color backgroundColor) {
		this.defaultBackgroundColor = backgroundColor;
		return this;
	}

	public ARow setDefaultFontStyle(FontStyle defaultFontStyle) {
		this.defaultFontStyle = defaultFontStyle;
		return this;
	}

	public ARow setBorder(Color color, float width) {
		for (ACell cell : cells)
			cell.setBorder(color, width);
		return this;
	}

	public ARow setBorderTop(Color color, float width) {
		for (ACell cell : cells)
			cell.setBorderTop(color, width);
		return this;
	}

	public ARow setBorderBottom(Color color, float width) {
		for (ACell cell : cells)
			cell.setBorderBottom(color, width);
		return this;
	}

	public ARow setBorderLeft(Color color, float width) {
		for (ACell cell : cells)
			cell.setBorderLeft(color, width);
		return this;
	}

	public ARow setBorderRight(Color color, float width) {
		for (ACell cell : cells)
			cell.setBorderRight(color, width);
		return this;
	}

	public List<ACell> getCells() {
		return cells;
	}

	// --- dependencies ---

	private ATable table;

	public ARow(ATable table) {
		this.table = table;
	}

}
