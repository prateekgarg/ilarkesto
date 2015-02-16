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
package ilarkesto.integration.svg;

import java.math.BigDecimal;
import java.util.Map;

public class Rect extends ASvgElement {

	private Point position;
	private Dimension size;
	private Style style;

	public Rect(Point position, Dimension size) {
		super();
		this.position = position;
		this.size = size;
	}

	@Override
	protected Point bottomRight() {
		return position.move(size);
	}

	@Override
	protected void provideAttributes(Map<String, String> attributes) {
		attributes.put("x", position.getX().toPlainString());
		attributes.put("y", position.getY().toPlainString());
		attributes.put("width", size.getW().toPlainString());
		attributes.put("height", size.getH().toPlainString());
		if (style != null) attributes.put("style", style.toString());
	}

	public Style getStyle() {
		if (style == null) style = new Style();
		return style;
	}

	public void setStyle(Style style) {
		this.style = style;
	}

	public Dimension getSize() {
		return size;
	}

	public Point getPosition() {
		return position;
	}

	public BigDecimal getWidth() {
		return getSize().getW();
	}

	public BigDecimal getHeight() {
		return getSize().getH();
	}

	public Point below() {
		return below(BigDecimal.ZERO);
	}

	public Point below(BigDecimal xOff) {
		return new Point(position.getX().add(xOff), position.getY().add(size.getH()));
	}

}
