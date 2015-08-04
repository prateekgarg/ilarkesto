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

import ilarkesto.core.time.Date;
import ilarkesto.core.time.DateAndTime;
import ilarkesto.core.time.Time;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.Format;

public abstract class AEditableDateAndTimeField extends AEditableTextBoxField<DateAndTime> {

	public static final DateTimeFormat format = DateTimeFormat.getFormat("dd.MM.yyyy HH:mm");

	@Override
	public DateAndTime prepareValue(String value) {
		if (value == null) return null;
		value = value.trim();
		if (value.isEmpty()) return null;

		if (value.length() < 11) {
			java.util.Date javaDate;
			try {
				javaDate = AEditableDateField.format.parse(value);
			} catch (Exception ex) {
				throw new RuntimeException(
						"Eingabe muß ein Datum (mit Uhrzeit) sein. TT.MM.JJJJ hh:mm, z.B. 01.01.2001 13:30");
			}
			return new DateAndTime(new Date(javaDate), new Time(14, 0));
		}

		java.util.Date javaDate;
		try {
			javaDate = format.parse(value);
		} catch (Exception ex) {
			throw new RuntimeException(
					"Eingabe muß ein Datum (mit Uhrzeit) sein. TT.MM.JJJJ hh:mm, z.B. 01.01.2001 13:30");
		}
		return new DateAndTime(javaDate);
	}

	@Override
	public Format getFormat() {
		return new DateBox.DefaultFormat(format);
	}

	@Override
	protected int getMaxLength() {
		return 16;
	}

}
