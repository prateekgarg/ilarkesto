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

import ilarkesto.core.time.DayAndMonth;

import com.google.gwt.i18n.client.DateTimeFormat;

// TODO calendar selector
public abstract class AEditableDayAndMonthField extends AEditableTextBoxField<DayAndMonth> {

	private static final DateTimeFormat format = DateTimeFormat.getFormat("dd.MM.");

	@Override
	public DayAndMonth prepareValue(String value) {
		if (value == null) return null;
		value = value.trim();
		if (value.isEmpty()) return null;
		if (!value.endsWith(".")) value += ".";
		java.util.Date javaDate;
		try {
			javaDate = format.parse(value);
		} catch (Exception ex) {
			throw new RuntimeException("Eingabe muß ein Datum sein. TT.MM, z.B. 03.08");
		}
		return new DayAndMonth(javaDate);
	}

	@Override
	protected int getMaxLength() {
		return 6;
	}

}
