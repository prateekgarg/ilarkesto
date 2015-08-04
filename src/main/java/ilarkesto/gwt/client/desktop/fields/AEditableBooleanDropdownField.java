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
package ilarkesto.gwt.client.desktop.fields;

import ilarkesto.core.base.Str;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AEditableBooleanDropdownField extends AEditableDropdownField {

	private String trueLabel = "Ja";
	private String falseLabel = "Nein";

	protected abstract void applyValue(boolean value);

	protected abstract boolean getValue();

	@Override
	public final void applyValue(String value) {
		applyValue(Str.isTrue(value));
	}

	@Override
	public Map<String, String> createOptions() {
		Map<String, String> ret = new LinkedHashMap<String, String>();
		ret.put(String.valueOf(true), getTrueLabel());
		ret.put(String.valueOf(false), getFalseLabel());
		return ret;
	}

	@Override
	public String getSelectedOptionKey() {
		return String.valueOf(getValue());
	}

	protected String getFalseLabel() {
		return falseLabel;
	}

	protected String getTrueLabel() {
		return trueLabel;
	}

	public AEditableBooleanDropdownField setLabels(String trueLabel, String falseLabel) {
		this.trueLabel = trueLabel;
		this.falseLabel = falseLabel;
		return this;
	}

	@Override
	public boolean isMandatory() {
		return true;
	}

}
