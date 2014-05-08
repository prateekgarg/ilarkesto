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
import ilarkesto.ui.web.HtmlBuilder.Tag;

public class Header extends AHtmlContainerElement {

	private Theme theme;

	public Header(JqmHtmlPage htmlPage) {
		super(htmlPage);
	}

	public Header setDataTheme(Theme theme) {
		this.theme = theme;
		return this;
	}

	public void addHomeLink(String href) {
		HtmlBuilder html = addHtmlRenderer();
		Tag a = html.startA(href);
		a.set("data-icon", "home");
		a.set("data-iconpos", "notext");
		a.set("data-direction", "reverse");
		html.text("Home");
		html.endA();
	}

	@Override
	protected void renderHeader(HtmlBuilder html, String id) {
		Tag div = html.startDIV().setId(id);
		div.setDataRole("header");
		if (theme != null) div.set("data-theme", theme.getName());
	}

}
