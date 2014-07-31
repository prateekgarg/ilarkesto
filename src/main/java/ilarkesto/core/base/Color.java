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
package ilarkesto.core.base;


public class Color {

	public static final Color WHITE = new Color(255, 255, 255);
	public static final Color LIGHT_GRAY = new Color(192, 192, 192);
	public static final Color GRAY = new Color(128, 128, 128);
	public static final Color DARK_GRAY = new Color(64, 64, 64);
	public static final Color BLACK = new Color(0, 0, 0);
	public static final Color RED = new Color(255, 0, 0);
	public static final Color PINK = new Color(255, 175, 175);
	public static final Color ORANGE = new Color(255, 200, 0);
	public static final Color YELLOW = new Color(255, 255, 0);
	public static final Color GREEN = new Color(0, 255, 0);
	public static final Color MAGENTA = new Color(255, 0, 255);
	public static final Color CYAN = new Color(0, 255, 255);
	public static final Color BLUE = new Color(0, 0, 255);

	private final int value;

	public Color(int r, int g, int b, int a) {
		super();
		validate(r);
		validate(g);
		validate(b);
		validate(a);
		value = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
	}

	public Color(final int red, final int green, final int blue) {
		this(red, green, blue, 255);
	}

	public Color(final float red, final float green, final float blue, final float alpha) {
		this((int) (red * 255 + .5), (int) (green * 255 + .5), (int) (blue * 255 + .5), (int) (alpha * 255 + .5));
	}

	public Color(final float red, final float green, final float blue) {
		this(red, green, blue, 1f);
	}

	public Color(final int argb) {
		value = argb;
	}

	public Color brighter() {
		return brighter(0.7d);
	}

	public Color brighter(double factor) {
		int r = getRed();
		int g = getGreen();
		int b = getBlue();

		int i = (int) (1.0 / (1.0 - factor));
		if (r == 0 && g == 0 && b == 0) { return new Color(i, i, i); }
		if (r > 0 && r < i) r = i;
		if (g > 0 && g < i) g = i;
		if (b > 0 && b < i) b = i;

		return new Color(Math.min((int) (r / factor), 255), Math.min((int) (g / factor), 255), Math.min(
			(int) (b / factor), 255));
	}

	public Color darker() {
		return darker(0.7d);
	}

	public Color darker(double factor) {
		return new Color(Math.max((int) (getRed() * factor), 0), Math.max((int) (getGreen() * factor), 0), Math.max(
			(int) (getBlue() * factor), 0));
	}

	public int getRgb() {
		return value;
	}

	public int getRed() {
		return (getRgb() >> 16) & 0xFF;
	}

	public int getGreen() {
		return (getRgb() >> 8) & 0xFF;
	}

	public int getBlue() {
		return (getRgb() >> 0) & 0xFF;
	}

	public int getAlpha() {
		return (getRgb() >> 24) & 0xff;
	}

	private static void validate(final int value) {
		if (value < 0 || value > 255) throw new IllegalArgumentException("Illegal color value: " + value);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Color && ((Color) obj).value == value;
	}

	@Override
	public int hashCode() {
		return value;
	}

	@Override
	public String toString() {
		return "#" + Integer.toString(value, 16);
	}

}
