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
package ilarkesto.ui.web.jqm;

import ilarkesto.ui.web.HtmlRenderer;

import java.util.ArrayList;
import java.util.List;

public abstract class AContainerElement extends AElement {

	private List<AElement> children = new ArrayList<AElement>();

	protected abstract void renderHeader(HtmlRenderer html);

	protected void renderFooter(HtmlRenderer html) {
		html.endDIV();
	}

	protected <E extends AElement> E addChild(E child) {
		children.add(child);
		return child;
	}

	@Override
	public final void render(HtmlRenderer html) {
		renderHeader(html);
		for (AElement child : children) {
			child.render(html);
		}
		renderFooter(html);
	}

}
