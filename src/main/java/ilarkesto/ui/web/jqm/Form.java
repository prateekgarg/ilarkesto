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

public class Form extends AHtmlContainerElement {

	private String id;
	private String name;
	private String action;
	private String method = "POST";
	private boolean ajax = false;

	public Form(String id, String action) {
		this.id = id;
		this.action = action;

		this.name = id;
	}

	public void addHidden(String name, String value) {
		addHtmlRenderer().INPUThidden(name, value);
	}

	public void addSubmitButton(String label, Theme theme) {
		HtmlRenderer html = addHtmlRenderer();
		Tag input = html.startTag("input", true).set("type", "submit");
		input.set("name", name);
		input.setValue(label);
		if (theme != null) input.set("data-theme", theme.getName());
		input.end();
	}

	public Textarea addTextarea(String id, String label) {
		return addChild(new Textarea(id, label));
	}

	public TextInput addTextInput(String id, String label) {
		return addChild(new TextInput(id, label));
	}

	public PasswordInput addPasswordInput(String id, String label) {
		return addChild(new PasswordInput(id, label));
	}

	public FlipToggleSwitch addFlipToggleSwitch(String id, String label, String trueLabel, String falseLabel) {
		return addChild(new FlipToggleSwitch(id, label, trueLabel, falseLabel));
	}

	public Select addSelect(String id, String label) {
		return addChild(new Select(id, label));
	}

	@Override
	protected void renderHeader(HtmlRenderer html) {
		Tag form = html.startFORM(new Url(action), method, name, false);
		form.setId(id);
		form.set("data-ajax", ajax);
		html.startFIELDSET();
	}

	@Override
	protected void renderFooter(HtmlRenderer html) {
		html.endFIELDSET();
		html.endFORM();
	}

}
