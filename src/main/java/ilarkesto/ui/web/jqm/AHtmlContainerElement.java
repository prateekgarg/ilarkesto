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

public abstract class AHtmlContainerElement extends AContainerElement {

	public void addLinkButton(String href, String text, String target, DataIcon icon, Theme theme) {
		HtmlRenderer html = addHtmlRenderer();
		Tag a = html.startA(href);
		a.setDataRole("button");
		a.setTarget(target);
		if (icon != null) a.setDataIcon(icon.getName());
		if (theme != null) a.set("data-theme", theme.getName());
		html.text(text);
		html.endA();
	}

	public void addText(Object text) {
		if (text == null) return;
		addHtmlRenderer().text(text);
	}

	public HtmlElement addHtml() {
		return addChild(new HtmlElement());
	}

	public HtmlRenderer addHtmlRenderer() {
		return addHtml().getRenderer();
	}

}
