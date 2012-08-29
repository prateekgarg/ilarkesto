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
import ilarkesto.ui.web.HtmlRenderer.Tag;

public class Page extends AHtmlContainerElement {

	private String[] style;

	public Page setStyle(String... style) {
		this.style = style;
		return this;
	}

	public Header addHeader() {
		return addChild(new Header());
	}

	public Content addContent() {
		return addChild(new Content());
	}

	public void addHeaderWithH1(String h1) {
		addHeader().addHtmlRenderer().H1(h1);
	}

	@Override
	protected void renderHeader(HtmlRenderer html) {
		html.nl();
		Tag div = html.startDIV();
		div.setDataRole("page");
		if (style != null) div.setStyle(style);
	}

}
