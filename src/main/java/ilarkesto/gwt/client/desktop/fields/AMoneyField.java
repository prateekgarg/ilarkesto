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

import ilarkesto.core.base.Utl;
import ilarkesto.core.localization.Localizer;
import ilarkesto.core.money.Money;
import ilarkesto.core.money.MultipleCurrenciesException;
import ilarkesto.gwt.client.desktop.Widgets;

import java.math.BigDecimal;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

public abstract class AMoneyField extends AField {

	protected abstract Money getValue() throws MultipleCurrenciesException;

	@Override
	public final IsWidget createDisplayWidget() {
		Money value;
		try {
			value = getValue();
		} catch (MultipleCurrenciesException ex) {
			return null;
		}
		if (value == null) return null;

		String text = value.format();

		Money percentageOf = getPercentageOf();
		if (percentageOf != null) {
			text += " / " + formatedPercent(percentageOf, value);
		}

		Label label = Widgets.textNoWrap(text);
		label.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		return label;
	}

	protected Money getPercentageOf() {
		return null;
	}

	@Override
	protected boolean isLabelAlignRight() {
		return true;
	}

	public static String formatedPercent(Money total, Money value) {
		if (total == null || total.isZero()) return null;
		if (value == null) return null;
		BigDecimal ret = value.getAmount().multiply(Utl.BD_HUNDRED)
				.divide(total.getAmount(), 2, BigDecimal.ROUND_HALF_UP);
		if (ret == null) return null;
		int scale = Utl.isBetween(ret, 99, 101) && ret.doubleValue() != 100d ? 2 : 0;
		return Localizer.get().formatPercent(ret, scale);
	}

}
