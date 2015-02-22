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

public class Glass {

	private Style style;

	private BigDecimal width;
	private BigDecimal height;

	public Glass() {
		style = new Style().setFill("#009999").setFillOpacity("0.05").setStroke("blue").setStrokeWidth("1")
				.setStrokeOpacity("0.1");
	}

	public Artefact create(String name, Point position) {
		return new Artefact(name, createRect(position));
	}

	public Glass setWidth(BigDecimal size) {
		this.width = size;
		return this;
	}

	public Glass setHeight(BigDecimal length) {
		this.height = length;
		return this;
	}

	private Rect createRect(Point position) {
		Rect rect = new Rect(position, new Dimension(width, height));
		rect.setStyle(style);
		return rect;
	}

}
