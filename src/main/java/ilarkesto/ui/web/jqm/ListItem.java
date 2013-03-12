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

	private String dataRole;

	private String linkHref;
	private String linkDataRel;
	private String linkRel;

	public ListItem(JqmHtmlPage htmlPage) {
		super(htmlPage);
	}

	public ListItem setLinkHref(String href) {
		this.linkHref = href;
		return this;
	}

	public ListItem setDataRole(String dataRole) {
		this.dataRole = dataRole;
		return this;
	}

	public ListItem setDataRoleToListDivider() {
		return setDataRole("list-divider");
	}

	public ListItem setLinkDataRel(String dataRel) {
		this.linkDataRel = dataRel;
		return this;
	}

	public ListItem setLinkRel(String linkRel) {
		this.linkRel = linkRel;
		return this;
	}

	public ListItem setLinkRelToExternal() {
		return setLinkRel("external");
	}

	@Override
	protected void renderHeader(HtmlRenderer html) {
		html.startLI().setDataRole(dataRole);
		if (linkHref != null) {
			Tag a = html.startA(linkHref);
			if (linkDataRel != null) a.setDataRel(linkDataRel);
			if (linkRel != null) a.setRel(linkRel);
		}
	}

	@Override
	protected void renderFooter(HtmlRenderer html) {
		if (linkHref != null) html.endA();
		html.endLI();
	}

}
