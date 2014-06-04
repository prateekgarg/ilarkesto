/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>, Artjom Kochtchi
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.core.money;

import ilarkesto.core.base.Args;
import ilarkesto.core.base.Str;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Working with money (amount + currency).
 */
public final class Money implements Comparable<Money>, Serializable {

	public static final transient String EUR = "EUR";
	public static final transient String USD = "USD";

	private long cent;
	private String currency;

	public Money(long value, long cent, String currency) {
		Args.assertNotNull(currency, "currency");
		this.cent = (value * 100) + cent;
		this.currency = currency.toUpperCase();
	}

	public Money(String s) {
		int amountCurrencySeparatorIdx = s.indexOf(' ');
		if (amountCurrencySeparatorIdx < 1) throw new IllegalArgumentException("Unsupported money format: " + s);

		this.currency = s.substring(amountCurrencySeparatorIdx).trim().toUpperCase();

		String amount = s.substring(0, amountCurrencySeparatorIdx).trim().replace(',', '.');
		this.cent = Math.round(Double.parseDouble(amount) * 100);
	}

	public Money(String amount, String currency) {
		Args.assertNotNull(currency, "currency");
		this.cent = Math.round(Double.parseDouble(amount.replace(',', '.')) * 100);
		this.currency = currency.toUpperCase();
	}

	public Money(double value, String currency) {
		Args.assertNotNull(currency, "currency");
		this.cent = Math.round(value * 100);
		this.currency = currency.toUpperCase();
	}

	public Money(BigDecimal value, String currency) {
		Args.assertNotNull(value, "value", currency, "currency");
		this.cent = value.movePointRight(2).longValueExact();
		this.currency = currency.toUpperCase();
	}

	public Money() {
		this(0, EUR);
	}

	public String getCurrency() {
		return currency;
	}

	public boolean isCurrency(String currency) {
		return this.currency.equals(currency);
	}

	public float getAmountAsFloat() {
		return cent / 100f;
	}

	public double getAmountAsDouble() {
		return cent / 100d;
	}

	public long getAmountAsCent() {
		return cent;
	}

	public boolean isPositive() {
		return cent >= 0;
	}

	public boolean isNegative() {
		return cent < 0;
	}

	public Money negate() {
		return new Money(0, cent * -1, currency);
	}

	public Money substract(Money... subtrahends) {
		return computeDifference(this, subtrahends);
	}

	public Money add(Money summand) {
		if (summand == null) return this;
		return computeSum(this, summand);
	}

	public Money invert() {
		return new Money(0, cent * -1, currency);
	}

	public Money multiplyAndRound(float factor) {
		return new Money(0, Math.round((cent * factor)), currency);
	}

	public Money divideAndRound(float divisor) {
		return new Money(0, Math.round((cent / divisor)), currency);
	}

	public String getAmountAsString(char decimalSeparator) {
		return getAmountAsString(decimalSeparator, null);
	}

	public String getAmountAsString(char decimalSeparator, String thousandsSeparator) {
		boolean neg = false;
		long c = cent;
		if (c < 0) {
			neg = true;
			c = Math.abs(c);
		}
		long value = c / 100;
		long rest = c - (value * 100);
		StringBuilder sb = new StringBuilder();
		if (neg) sb.append("-");
		sb.append(Str.formatWithThousandsSeparator(value, thousandsSeparator));
		sb.append(decimalSeparator);
		if (rest < 10) sb.append('0');
		sb.append(rest);
		return sb.toString();
	}

	public String toString(String lang) {
		if (lang != null) {
			lang = lang.toLowerCase();
			if (lang.equals("de")) return getAmountAsString(',', ".") + ' ' + currency;
		}
		return toString('.');
	}

	public String toString(char decimalSeparator) {
		return getAmountAsString(decimalSeparator) + ' ' + currency;
	}

	public boolean isLessThen(Money m) {
		return cent < m.cent;
	}

	public boolean isLessThenOrEqualTo(Money m) {
		return cent <= m.cent;
	}

	public boolean isGreaterThen(Money m) {
		return cent > m.cent;
	}

	public boolean isGreaterThenOrEqualTo(Money m) {
		return cent >= m.cent;
	}

	@Override
	public String toString() {
		return toString('.');
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		Money other = (Money) obj;
		return cent == other.cent && currency.equals(other.currency);
	}

	private transient int hashCode;

	@Override
	public int hashCode() {
		if (hashCode == 0) {
			hashCode = 23;
			hashCode = hashCode * 37 + (int) cent;
			hashCode = hashCode * 37 + currency.hashCode();
		}
		return hashCode;
	}

	@Override
	public int compareTo(Money o) {
		if (cent > o.cent) return 1;
		if (cent < o.cent) return -1;
		return 0;
	}

	// --- ---

	public static Money computeAvg(String currency, Money... moneys) {
		return computeSum(currency, moneys).divideAndRound(moneys.length);
	}

	public static Money computeSum(Money... moneys) {
		if (moneys.length == 0) throw new IllegalArgumentException("At least one money required for computing a sum.");
		if (moneys.length == 1) return moneys[0];
		return computeSum(moneys[0].currency, moneys);
	}

	public static Money computeSum(String currency, Money... summands) {
		if (summands.length == 1) return summands[0];
		long sum = 0;
		for (Money summand : summands) {
			if (summand == null) continue;
			if (!summand.isCurrency(currency))
				throw new IllegalArgumentException("Summand needs to be in currency " + currency + ": " + summand);
			sum += summand.cent;
		}
		return new Money(0, sum, currency);
	}

	public static Money computeDifference(Money minuend, Money... subtrahends) {
		if (subtrahends == null || subtrahends.length == 0) return minuend;

		long difference = minuend.cent;
		for (Money subtrahend : subtrahends) {
			if (subtrahend == null) continue;
			if (!subtrahend.isCurrency(minuend.currency))
				throw new IllegalArgumentException("Subtrahend needs to be in currency of the minuend "
						+ minuend.currency + ": " + subtrahend);
			difference -= subtrahend.cent;
		}
		return new Money(0, difference, minuend.currency);
	}

	public static Money[] createArray(int size, Money initialValue) {
		Money[] moneys = new Money[size];
		for (int i = 0; i < size; i++)
			moneys[i] = initialValue;
		return moneys;
	}

}
