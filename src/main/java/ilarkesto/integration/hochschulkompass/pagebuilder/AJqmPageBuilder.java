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
package ilarkesto.integration.hochschulkompass.pagebuilder;

import ilarkesto.ui.web.jqm.Content;
import ilarkesto.ui.web.jqm.JqmHtmlPage;
import ilarkesto.ui.web.jqm.Page;

public abstract class AJqmPageBuilder {

	protected Context context;
	private JqmHtmlPage htmlPage;

	public AJqmPageBuilder(Context context) {
		super();
		this.context = context;
	}

	protected abstract String getTitle();

	protected abstract void fillContent(Content content);

	protected void fillPage(Page page) {
		page.addHeaderWithH1(getTitle());
		Content content = page.addContent();
		fillContent(content);
	}

	public final JqmHtmlPage buildPage() {
		htmlPage = new JqmHtmlPage(getTitle(), context.getLanguage());
		Page page = htmlPage.addPage();
		fillPage(page);
		return htmlPage;
	}

}
