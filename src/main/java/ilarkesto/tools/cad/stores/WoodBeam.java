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
package ilarkesto.tools.cad.stores;

import ilarkesto.integration.svg.Dimension;
import ilarkesto.integration.svg.Point;
import ilarkesto.integration.svg.Rect;
import ilarkesto.integration.svg.Style;
import ilarkesto.tools.cad.Artefact;

import java.math.BigDecimal;

public class WoodBeam {

	private Style style;

	private BigDecimal size;
	private BigDecimal length;
	private boolean vertical;

	public WoodBeam() {
		style = new Style().setFillNone().setStroke("#b69b4c").setStrokeWidth("1");
	}

	public Artefact create(String name, Point position) {
		return new Artefact(name, createRect(position));
	}

	public WoodBeam setSize(BigDecimal size) {
		this.size = size;
		return this;
	}

	public WoodBeam setLength(BigDecimal length) {
		this.length = length;
		return this;
	}

	public WoodBeam setVertical() {
		this.vertical = true;
		return this;
	}

	public WoodBeam setHorizontal() {
		this.vertical = false;
		return this;
	}

	private Rect createRect(Point position) {
		Rect rect;
		if (vertical) {
			rect = new Rect(position, new Dimension(size, length));
		} else {
			rect = new Rect(position, new Dimension(length, size));
		}
		rect.setStyle(style);
		return rect;
	}
}
