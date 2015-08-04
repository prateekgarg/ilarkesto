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
import ilarkesto.core.localization.Localizer;

import java.math.BigDecimal;

public abstract class AEditableDecimalField extends AEditableTextBoxField<BigDecimal> {

	private boolean thousandsSeparator = true;
	private Integer scale;

	public abstract BigDecimal getBigDecimalValue();

	@Override
	public String getValue() {
		return Localizer.get().format(getBigDecimalValue(), thousandsSeparator, scale, true);
	}

	@Override
	public BigDecimal prepareValue(String value) {
		if (value == null) return null;
		value = value.trim();
		if (value.isEmpty()) return null;
		value = value.replace(".", "");
		value = value.replace(',', '.');
		try {
			return new BigDecimal(value);
		} catch (NumberFormatException ex) {
			throw new RuntimeException("Eingabe einer Dezimalzahl erforderlich.");
		}
	}

	@Override
	public void validateValue(BigDecimal value) throws RuntimeException {
		super.validateValue(value);
		if (value == null) return;

		BigDecimal min = getMinValue();
		if (min != null && value.compareTo(min) < 0)
			throw new RuntimeException("Wert darf nicht kleiner " + Str.format(min) + " sein.");

		BigDecimal max = getMaxValue();
		if (max != null && value.compareTo(max) > 0)
			throw new RuntimeException("Wert darf nicht größer " + Str.format(min) + " sein.");
	}

	@Override
	protected int getMaxLength() {
		return 23;
	}

	protected BigDecimal getMinValue() {
		return BigDecimal.ZERO;
	}

	protected BigDecimal getMaxValue() {
		return null;
	}

	@Override
	protected boolean isDisplayValueAlignedRight() {
		return true;
	}

	@Override
	protected boolean isLabelAlignRight() {
		return true;
	}

	public AEditableDecimalField setThousandsSeparator(boolean thousandsSeparator) {
		this.thousandsSeparator = thousandsSeparator;
		return this;
	}

	public AEditableDecimalField setScale(Integer scale) {
		this.scale = scale;
		return this;
	}

}
