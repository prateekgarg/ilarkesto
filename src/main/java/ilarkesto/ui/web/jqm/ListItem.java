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

public class ListItem extends AHtmlContainerElement {

	private String href;
	private String dataRel;

	public ListItem setHref(String href) {
		this.href = href;
		return this;
	}

	public ListItem setDataRel(String dataRel) {
		this.dataRel = dataRel;
		return this;
	}

	@Override
	protected void renderHeader(HtmlRenderer html) {
		html.startLI();
		if (href != null) {
			Tag a = html.startA(href);
			if (dataRel != null) a.setDataRel(dataRel);
		}
	}

	@Override
	protected void renderFooter(HtmlRenderer html) {
		if (href != null) html.endA();
		html.endLI();
	}

}
