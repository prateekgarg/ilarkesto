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
package ilarkesto.integration.svg;

import ilarkesto.core.base.Args;

import java.math.BigDecimal;

public class Dimension {

	private BigDecimal w;
	private BigDecimal h;

	public Dimension(BigDecimal w, BigDecimal h) {
		Args.assertNotNull(w, "w", h, "h");
		this.w = w;
		this.h = h;
	}

	public Dimension(long w, long h) {
		this(new BigDecimal(w), new BigDecimal(h));
	}

	public BigDecimal getW() {
		return w;
	}

	public BigDecimal getH() {
		return h;
	}

	@Override
	public int hashCode() {
		return w.hashCode() * h.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Dimension)) return false;
		Dimension other = (Dimension) obj;
		return w.equals(other.w) && h.equals(other.h);
	}
}
