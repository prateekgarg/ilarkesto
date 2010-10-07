package ilarkesto.pdf;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ARow {

	private List<ACell> cells = new ArrayList<ACell>();
	private Color defaultBackgroundColor;

	public ACell cell() {
		ACell cell = table.cell();
		cell.setBackgroundColor(defaultBackgroundColor);
		cells.add(cell);
		return cell;
	}

	public ACell cell(Object text, FontStyle fontStyle) {
		ACell cell = table.cell(text, fontStyle);
		cell.setBackgroundColor(defaultBackgroundColor);
		cells.add(cell);
		return cell;
	}

	public ACell cell(Object text) {
		ACell cell = table.cell(text);
		cell.setBackgroundColor(defaultBackgroundColor);
		cells.add(cell);
		return cell;
	}

	public ARow setDefaultBackgroundColor(Color backgroundColor) {
		this.defaultBackgroundColor = backgroundColor;
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

	// --- dependencies ---

	private ATable table;

	public ARow(ATable table) {
		this.table = table;
	}

}
