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

public class FlipToggleSwitch extends AFieldElement {

	private String trueLabel;
	private String falseLabel;
	private boolean value;

	public FlipToggleSwitch(String id, String label, String trueLabel, String falseLabel) {
		super(id, label);
		this.trueLabel = trueLabel;
		this.falseLabel = falseLabel;
	}

	@Override
	protected void renderElement(HtmlRenderer html) {
		Tag select = html.startSELECT(name);
		select.setId(id);
		select.setDataRole("slider");

		html.OPTION(String.valueOf(false), falseLabel, !value);
		html.OPTION(String.valueOf(true), trueLabel, value);

		html.endSELECT();
	}

	public FlipToggleSwitch setValue(Boolean value) {
		this.value = value == null ? false : value;
		return this;
	}

}
