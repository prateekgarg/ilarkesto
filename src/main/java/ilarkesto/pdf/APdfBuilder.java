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

import ilarkesto.core.base.Args;

public abstract class APdfBuilder extends APdfContainerElement {

	enum Alignment {
		LEFT, RIGHT, CENTER, JUSTIFIED
	}

	protected FontStyle fontStyle = new FontStyle();
	protected float marginTop = 15f;
	protected float marginBottom = 20f;
	protected float marginLeft = 20f;
	protected float marginRight = 20f;
	protected float pageWidth = 210f;
	protected float pageHeight = 297f;

	public APdfBuilder() {
		super(null);
	}

	@Override
	protected APdfBuilder getPdf() {
		return this;
	}

	public abstract APdfBuilder newPage();

	public abstract boolean isNewPage();

	public APdfBuilder setFontStyle(FontStyle fontStyle) {
		Args.assertNotNull(fontStyle, "fontStyle");
		this.fontStyle = fontStyle;
		return this;
	}

	public FontStyle getFontStyle() {
		return fontStyle;
	}

	public APdfBuilder setMarginTop(float marginTop) {
		this.marginTop = marginTop;
		return this;
	}

	public APdfBuilder setMarginBottom(float marginBottom) {
		this.marginBottom = marginBottom;
		return this;
	}

	public APdfBuilder setMarginLeft(float marginLeft) {
		this.marginLeft = marginLeft;
		return this;
	}

	public APdfBuilder setMarginRight(float marginRight) {
		this.marginRight = marginRight;
		return this;
	}

	public APdfBuilder setPageSizeToA4Landscape() {
		return setPageSize(297, 210);
	}

	private APdfBuilder setPageSize(float width, float height) {
		this.pageWidth = width;
		this.pageHeight = height;
		return this;
	}

	// --- helper ---

	protected static final int dpi = 72;

	public static float mmToPoints(double mm) {
		return (float) ((mm / 25.4f) * dpi);
	}

}
