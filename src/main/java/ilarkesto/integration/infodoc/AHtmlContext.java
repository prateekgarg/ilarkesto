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
package ilarkesto.integration.infodoc;

public abstract class AHtmlContext {

	public String getCommentStyle() {
		return "color: grey; font-style: italic;";
	}

	public String getElementDepthStyle(int depth) {
		return "margin-left: " + (depth * 42) + "px;";
	}

	public String getColor(int depth) {
		switch (depth) {
			case 0:
				return "#0099CC";
			case 1:
				return "#669900";
			case 2:
				return "#9933CC";
			case 3:
				return "#FF8800";
		}
		return "black";
	}

	public abstract String getHref(String ref);

}
