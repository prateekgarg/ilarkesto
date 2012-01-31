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

public class Listview extends AContainerElement {

	private boolean dataInset = true;
	private boolean dataFilter;
	private Theme dataTheme;

	public Listview setDataFilter(boolean dataFilter) {
		this.dataFilter = dataFilter;
		return this;
	}

	public ListDivider addDivider(Object text) {
		ListDivider divider = addDivider();
		divider.addText(text);
		return divider;
	}

	public ListDivider addDivider() {
		return addChild(new ListDivider());
	}

	public ListItem addItem(String href, Object text) {
		ListItem item = addItem();
		item.setHref(href);
		item.addText(text);
		return item;
	}

	public ListItem addItem() {
		return addChild(new ListItem());
	}

	@Override
	protected void renderHeader(HtmlRenderer html) {
		Tag ul = html.startUL();
		ul.setDataRole("listview");
		ul.set("data-inset", dataInset);
		if (dataTheme != null) ul.set("data-theme", dataTheme.getName());
		if (dataFilter) ul.set("data-filter", dataFilter);
	}

	@Override
	protected void renderFooter(HtmlRenderer html) {
		html.endUL();
	}

	public Listview setDataTheme(Theme dataTheme) {
		this.dataTheme = dataTheme;
		return this;
	}

}
