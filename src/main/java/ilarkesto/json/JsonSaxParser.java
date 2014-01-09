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
package ilarkesto.json;

import java.io.BufferedReader;

public class JsonSaxParser {

	private BufferedReader in;
	private Handler handler;

	private JsonSaxParser(BufferedReader in, Handler handler) {
		super();
		this.in = in;
		this.handler = handler;
		// parse();
	}

	public static void parse(BufferedReader in, Handler handler) {
		new JsonSaxParser(in, handler);
	}

	public static interface Handler {

	}

}
