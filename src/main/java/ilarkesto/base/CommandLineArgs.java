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
package ilarkesto.base;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class CommandLineArgs {

	private LinkedList<String> args;

	public CommandLineArgs(String[] args) {
		this.args = new LinkedList(Arrays.asList(args));
	}

	public boolean popFlag(String flag) {
		flag = "--" + flag;
		if (!args.contains(flag)) return false;
		args.remove(flag);
		return true;
	}

	public boolean containsAny() {
		return !args.isEmpty();
	}

	public String popParameter() {
		Iterator<String> iterator = args.iterator();
		while (iterator.hasNext()) {
			String arg = iterator.next();
			if (arg.startsWith("-")) continue;
			iterator.remove();
			return arg;
		}
		return null;
	}

}
