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

import org.testng.annotations.Test;

public class MoneyTest extends ATest {

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
