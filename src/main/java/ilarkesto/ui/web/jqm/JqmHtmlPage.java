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

import java.io.PrintWriter;

public class JqmHtmlPage extends AContainerElement {

	private String title;
	private String language;

	public JqmHtmlPage(String title, String language) {
		super();
		this.title = title;
		this.language = language;
	}

	public Page addPage() {
		return addChild(new Page());
	}

	@Override
	protected void renderHeader(HtmlRenderer html) {
		html.startHTML();
		html.startHEAD(title, language);

		html.LINKcss("jqm/jquery.mobile.css");
		html.SCRIPTjavascript("jqm/jquery.js", null);
		html.SCRIPTjavascript("jqm/jquery.mobile.js", null);

		html.endHEAD();
		html.startBODY();
	}

	@Override
	protected void renderFooter(HtmlRenderer html) {
		html.endBODY();
		html.endHTML();
	}

	public void write(PrintWriter out, String encoding) {
		HtmlRenderer html = new HtmlRenderer(out, encoding);
		render(html);
		html.flush();
	}

}
