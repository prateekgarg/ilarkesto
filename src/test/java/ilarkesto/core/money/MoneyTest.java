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
package ilarkesto.core.money;

import ilarkesto.testng.ATest;

import java.math.BigDecimal;

import org.testng.annotations.Test;

public class MoneyTest extends ATest {

	@Test
	public void getPercentage() {
		assertEquals(new Money("100 EUR").getPercentage(new BigDecimal("100")), new Money("100 EUR"));
		assertEquals(new Money("100 EUR").getPercentage(new BigDecimal("0")), new Money("0 EUR"));
		assertEquals(new Money("23.23 EUR").getPercentage(new BigDecimal("200")), new Money("46.46 EUR"));
		assertEquals(new Money("100 EUR").getPercentage(new BigDecimal("0.009")), new Money("0.01 EUR"));
	}

	@Test
	public void percentageOf() {
		assertEquals(new Money("1 EUR").getPercentageOf(new Money("5 EUR")), new BigDecimal("20"));
		assertEquals(new Money("5 EUR").getPercentageOf(new Money("100 EUR")), new BigDecimal("5"));
		assertEquals(new Money("23 EUR").getPercentageOf(new Money("23 EUR")), new BigDecimal("100"));
		assertEquals(new Money("2 EUR").getPercentageOf(new Money("1 EUR")), new BigDecimal("200"));
	}

	@Test
	public void percent() {
		assertEquals(new Money("1 EUR").percent(new BigDecimal(1)), new Money("0.01 EUR"));
		assertEquals(new Money("1 EUR").percent(new BigDecimal("1.5")), new Money("0.02 EUR"));
	}

	@Test
	public void add() throws MultipleCurrenciesException {
		assertEquals(new Money("1 EUR").add(new Money("1 EUR")), new Money("2 EUR"));

		try {
			new Money("1 EUR").add(new Money("1 USD"));
			failExceptionExpected(MultipleCurrenciesException.class);
		} catch (MultipleCurrenciesException ex) {
			// expected
		}
	}

	@Test
	public void getAmountAsFloat() {
		assertEquals(new Money("23.42 EUR").getAmountAsFloat(), 23.42f, 0f);
		assertEquals(new Money("1 EUR").getAmountAsFloat(), 1f, 0f);
		assertEquals(new Money("1000000 EUR").getAmountAsFloat(), 1000000f, 0f);
		assertEquals(new Money("-0.01 EUR").getAmountAsFloat(), -0.01f, 0f);
	}

	@Test
	public void getAmountAsDouble() {
		assertEquals(new Money("23.42 EUR").getAmountAsDouble(), 23.42d, 0d);
		assertEquals(new Money("1 EUR").getAmountAsDouble(), 1d, 0d);
		assertEquals(new Money("1000000 EUR").getAmountAsDouble(), 1000000d, 0d);
		assertEquals(new Money("-0.01 EUR").getAmountAsDouble(), -0.01d, 0d);
	}

	@Test
	public void constructors() {
		assertCents(new Money(23, 42, "EUR"), 2342, "EUR");
		assertCents(new Money(1, 100, "EUR"), 200, "EUR");
		assertCents(new Money(1, -100, "EUR"), 0, "EUR");

		assertCents(new Money(23.42d, "EUR"), 2342, "EUR");
		assertCents(new Money(-1d, "EUR"), -100, "EUR");
		assertCents(new Money(0.5d, "EUR"), 50, "EUR");
		assertCents(new Money(0.001d, "EUR"), 0, "EUR");
		assertCents(new Money(0.005d, "EUR"), 1, "EUR");

		assertCents(new Money("23.42", "EUR"), 2342, "EUR");
		assertCents(new Money("-1", "EUR"), -100, "EUR");
		assertCents(new Money("1000000", "EUR"), 100000000, "EUR");
		assertCents(new Money("0.001", "EUR", true), 0, "EUR");
		assertCents(new Money("0.005", "EUR", true), 1, "EUR");

		try {
			assertCents(new Money("0.001", "EUR"), 0, "EUR");
			fail();
		} catch (ArithmeticException ex) {
			// expected
		}
		try {
			assertCents(new Money("0.005", "EUR"), 1, "EUR");
			fail();
		} catch (ArithmeticException ex) {
			// expected
		}
		assertCents(new Money("1,000", "EUR"), 100, "EUR");

		assertCents(new Money("23.42 EUR"), 2342, "EUR");
		assertCents(new Money("-1 EUR"), -100, "EUR");
		assertCents(new Money("1000000 EUR"), 100000000, "EUR");
		assertCents(new Money("0.001 EUR", true), 0, "EUR");
		assertCents(new Money("0.005 EUR", true), 1, "EUR");

		try {
			assertCents(new Money("0.001 EUR"), 0, "EUR");
			fail();
		} catch (ArithmeticException ex) {
			// expected
		}
		try {
			assertCents(new Money("0.005 EUR"), 1, "EUR");
			fail();
		} catch (ArithmeticException ex) {
			// expected
		}
		assertCents(new Money("1,000 EUR"), 100, "EUR");

	}

	@Test
	public void testToString() {
		assertEquals(new Money(42, 42, "USD").toString(), "42.42 USD");
		assertEquals(new Money(1000000, 0, "USD").toString(), "1000000.00 USD");
		assertEquals(new Money(-1, 0, "USD").toString(), "-1.00 USD");
	}

	private void assertCents(Money money, long cents, String currency) {
		assertEquals(money.getAmountAsCent(), cents);
		assertEquals(money.getCurrency(), currency);
	}
}
