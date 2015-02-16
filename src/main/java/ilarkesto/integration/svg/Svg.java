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

import ilarkesto.ui.web.HtmlBuilder;

import java.util.Map;

public class Svg extends ASvgContainer {

	private Dimension size;

	public String toHtml() {
		HtmlBuilder html = new HtmlBuilder();
		html.startHTML();
		html.startBODY();
		html.html(toString());
		html.endBODY();
		html.endHTML();
		return html.toString();
	}

	@Override
	protected void provideAttributes(Map<String, String> attributes) {
		if (size == null) {
			Point bottomRight = bottomRight();
			attributes.put("width", bottomRight.getX().toPlainString());
			attributes.put("height", bottomRight.getY().toPlainString());
		} else {
			attributes.put("width", size.getW().toPlainString());
			attributes.put("height", size.getH().toPlainString());
		}
	}

}
