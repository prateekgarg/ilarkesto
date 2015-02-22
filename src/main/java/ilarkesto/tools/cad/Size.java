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
package ilarkesto.tools.cad;

import java.math.BigDecimal;

public class Size {

	private String name;
	private BigDecimal value;

	public Size(String name, BigDecimal value) {
		super();
		this.name = name;
		this.value = value;
	}

	public Size(String name, long value) {
		this(name, new BigDecimal(value));
	}

	public BigDecimal getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

}
