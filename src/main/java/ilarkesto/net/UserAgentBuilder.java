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
package ilarkesto.net;

import ilarkesto.core.base.Str;
import ilarkesto.core.time.Tm;

import java.util.Random;

public class UserAgentBuilder {

	private StringBuilder sb = new StringBuilder();

	public UserAgentBuilder(String comment) {
		add("Mozilla", "5.0", comment);
	}

	public UserAgentBuilder add(String name, String version, String comment) {
		if (sb.length() > 0) sb.append(' ');
		sb.append(name);
		sb.append('/').append(version);
		if (!Str.isBlank(comment)) sb.append(" (").append(comment).append(")");
		return this;
	}

	public static String random() {
		return new UserAgentBuilder(randomOs()).addRandomEngine().addRandomBrowser().toString();
	}

	private UserAgentBuilder addRandomEngine() {
		switch (randomInt(0, 3)) {
			case 0:
				return add("Gecko", "20130401", null);
			case 1:
				return add("Gecko", "20130101", null);
			case 2:
				return add("AppleWebKit", "537.36", "KHTML, like Gecko");
			default:
				return add("Gecko", "20100101", null);
		}
	}

	private UserAgentBuilder addRandomBrowser() {
		switch (randomInt(0, 3)) {
			case 0:
				return add("Chrome", randomInt(36, 42) + ".0." + randomInt(2001, 2230) + ".0", null);
			default:
				return add("Firefox", randomInt(20, 38) + ".0", null);
		}
	}

	private static String randomOs() {
		return randomElement("Windows NT 6.3; rv:36.0", "Windows; U; Windows NT 5.1; de; rv:1.9.2.3",
			"Windows NT 6.0; WOW64; rv:24.0", "Macintosh; Intel Mac OS X 10_10; rv:33.0", "X11; Linux i586; rv:31.0",
			"X11; Ubuntu; Linux x86_64; rv:24.0");
	}

	private static final Random random = new Random(Tm.getCurrentTimeMillis());

	private static String randomElement(String... elements) {
		return elements[random.nextInt(elements.length)];
	}

	private static int randomInt(int min, int max) {
		return random.nextInt(max - min + 1) + min;
	}

	@Override
	public String toString() {
		return sb.toString();
	}

}
