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
package ilarkesto.protocol;

import ilarkesto.core.base.Str;

public class Message {

	public static enum Type {
		INFO, ERROR
	}

	private Type type;
	private Object[] s;

	Message(Type type, Object... s) {
		super();
		this.type = type;
		this.s = s;
	}

	public boolean isImportant() {
		return type != Type.INFO;
	}

	public String getText() {
		return Str.formatMessage(s);
	}

	@Override
	public String toString() {
		return type + " " + getText();
	}

}
