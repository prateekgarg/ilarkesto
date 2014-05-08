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

import ilarkesto.ui.web.HtmlBuilder;

public abstract class AElement {

	private static long ids;

	private JqmHtmlPage htmlPage;
	private String id;

	protected abstract void render(HtmlBuilder html, String id);

	public final void render(HtmlBuilder html) {
		render(html, id);
	}

	public AElement(JqmHtmlPage htmlPage) {
		super();
		this.htmlPage = htmlPage;
	}

	public JqmHtmlPage getHtmlPage() {
		return htmlPage;
	}

	public final String getId() {
		if (id == null) id = "jqm" + (++ids);
		return id;
	}

	public AElement setId(String id) {
		this.id = id;
		return this;
	}

}
