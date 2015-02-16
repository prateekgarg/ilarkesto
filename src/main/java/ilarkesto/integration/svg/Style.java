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

public class Style {

	private String fill;
	private String fillOpacity;
	private String stroke;
	private String strokeWidth;

	public Style setFill(String fill) {
		this.fill = fill;
		return this;
	}

	public Style setFillOpacity(String fillOpacity) {
		this.fillOpacity = fillOpacity;
		return this;
	}

	public Style setFillNone() {
		return setFill("none");
	}

	public Style setStroke(String stroke) {
		this.stroke = stroke;
		return this;
	}

	public Style setStrokeWidth(String strokeWidth) {
		this.strokeWidth = strokeWidth;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (fill != null) sb.append("fill:").append(fill).append("; ");
		if (fillOpacity != null) sb.append("fill-opacity:").append(fillOpacity).append("; ");
		if (stroke != null) sb.append("stroke:").append(stroke).append("; ");
		if (strokeWidth != null) sb.append("stroke-width:").append(strokeWidth).append("; ");
		return sb.toString();
	}

}
