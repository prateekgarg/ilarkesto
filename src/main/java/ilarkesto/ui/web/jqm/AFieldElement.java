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

public abstract class AFieldElement extends AElement {

	protected String id;
	protected String name;
	protected String label;

	public AFieldElement(String id, String label) {
		this.id = id;
		this.label = label;

		this.name = id;
	}

	protected abstract void renderElement(HtmlRenderer html);

	@Override
	public void render(HtmlRenderer html) {
		html.startDIV().setDataRole("fieldcontain");

		html.LABEL(id, label);

		renderElement(html);

		html.endDIV();
	}

	public AFieldElement setName(String name) {
		this.name = name;
		return this;
	}

}
