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

import ilarkesto.core.localization.Localizer;
import ilarkesto.core.money.Money;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

public abstract class AEditableMoneyField extends AEditableTextBoxField<Money> {

	private Money displaySuffixPercentageReference;

	protected abstract Money getMoneyValue();

	@Override
	protected final String getValue() {
		return Localizer.get().format(getMoneyValue());
	}

	@Override
	public Money prepareValue(String value) {
		if (value == null) return null;

		value = value.trim();
		if (value.isEmpty()) return null;
		value = value.replace(".", "");

		// TODOP: Noch fehleranfaellig bei Sonderzeichen, z.B.bei Eingabe von 500:00 EUR

		String patternEnd = "(\\D+)$";
		RegExp regExpEndingWithNonDigit = RegExp.compile(patternEnd);
		MatchResult m = regExpEndingWithNonDigit.exec(value);
		boolean nonDigitFound = (m != null);

		RegExp regExp;
		if (nonDigitFound) {
			regExp = RegExp.compile("(\\d+[\\.,]?\\d?\\d?)(\\s\\w*)");
		} else {
			regExp = RegExp.compile("(\\d+[\\.,]?\\d?\\d?)");
		}

		MatchResult matcher = regExp.exec(value);
		boolean matchFound = (matcher != null);

		if (!matchFound) { throw new RuntimeException("Bitte richtiges Format eingeben."); }

		if (!value.contains(" ")) value += " " + Money.EUR;

		value = value.replace("€", "EUR");
		value = value.replace("$", "USD");

		return new Money(value);
	}

	@Override
	protected boolean isDisplayValueAlignedRight() {
		return true;
	}

	@Override
	protected int getMaxLength() {
		return 15;
	}

	@Override
	public String getSuffix() {
		if (displaySuffixPercentageReference != null) {
			Money value = getMoneyValue();
			if (value != null) return " | " + AMoneyField.formatedPercent(displaySuffixPercentageReference, value);
		}
		return super.getSuffix();
	}

	public AEditableMoneyField setDisplaySuffixPercentageReference(Money displaySuffixPercentageReference) {
		this.displaySuffixPercentageReference = displaySuffixPercentageReference;
		return this;
	}

	@Override
	protected boolean isLabelAlignRight() {
		return true;
	}

}
