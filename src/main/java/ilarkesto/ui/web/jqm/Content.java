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

import ilarkesto.base.Url;
import ilarkesto.ui.web.HtmlRenderer;
import ilarkesto.ui.web.HtmlRenderer.Tag;

import java.util.UUID;

public class Content extends AHtmlContainerElement {

	private Integer maxWidth;

	public Content setMaxWidth(Integer maxWidth) {
		this.maxWidth = maxWidth;
		return this;
	}

	public Panel addPanel() {
		return addChild(new Panel());
	}

	public Form addForm(Url action) {
		return addForm(action.toString());
	}

	public Form addForm(String action) {
		return addChild(new Form("jqmform_" + UUID.randomUUID().toString(), action));
	}

	public Form addForm(String id, String action) {
		return addChild(new Form(id, action));
	}

	public Listview addListview() {
		return addChild(new Listview());
	}

	public Popup addPopup(String id) {
		return addChild(new Popup(id));
	}

	@Override
	protected void renderHeader(HtmlRenderer html) {
		Tag div = html.startDIV();
		div.setDataRole("content");
		if (maxWidth != null) div.setStyle("max-width: " + maxWidth + "px", "margin: 0 auto");
	}

}
