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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class ASvgContainer extends ASvgElement {

	private List<ASvgElement> children = new ArrayList<ASvgElement>();

	public ASvgContainer add(ASvgElement element) {
		children.add(element);
		return this;
	}

	public final Collection<ASvgElement> getChildren() {
		return children;
	}

}
