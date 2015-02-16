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

public class Point {

	private BigDecimal x;
	private BigDecimal y;

	public Point(BigDecimal x, BigDecimal y) {
		Args.assertNotNull(x, "x", y, "y");
		this.x = x;
		this.y = y;
	}

	public Point(long x, long y) {
		this(new BigDecimal(x), new BigDecimal(y));
	}

	public BigDecimal getX() {
		return x;
	}

	public BigDecimal getY() {
		return y;
	}

	@Override
	public int hashCode() {
		return x.hashCode() * y.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Point)) return false;
		Point other = (Point) obj;
		return x.equals(other.x) && y.equals(other.y);
	}

	public Point right(BigDecimal offset) {
		return new Point(x.add(offset), y);
	}

	public Point left(BigDecimal offset) {
		return new Point(x.subtract(offset), y);
	}

	public Point move(BigDecimal x, BigDecimal y) {
		return new Point(this.x.add(x), this.y.add(y));
	}

	public Point move(Dimension size) {
		return move(size.getW(), size.getH());
	}

	public Point max(Point other) {
		return new Point(x.max(other.x), y.max(other.y));
	}

	public Point down(BigDecimal offset) {
		return new Point(x, y.add(offset));
	}

	public Point up(BigDecimal offset) {
		return new Point(x, y.subtract(offset));
	}

}
