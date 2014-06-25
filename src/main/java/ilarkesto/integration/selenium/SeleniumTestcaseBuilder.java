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
package ilarkesto.integration.selenium;

import java.util.ArrayList;
import java.util.List;

public class SeleniumTestcaseBuilder {

	private List<Command> commands = new ArrayList<Command>();

	public Command type(String target, String value) {
		return command("type", target, value);
	}

	public Command open(String url) {
		return command("open", url);
	}

	public Command command(String command, String target, String value) {
		return command(command, target).setValue(value);
	}

	public Command command(String command, String target) {
		return command(command).setTarget(target);
	}

	public Command command(String command) {
		Command c = new Command(command);
		commands.add(c);
		return c;
	}

	public class Command {

		private String command;
		private String target;
		private String value;

		public Command(String command) {
			super();
			this.command = command;
		}

		public Command setTarget(String target) {
			this.target = target;
			return this;
		}

		public Command setValue(String value) {
			this.value = value;
			return this;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("\n<tr>");
			sb.append("\n  <td>").append(command).append("</td>");
			sb.append("\n  <td>").append(target == null ? "" : target).append("</td>");
			sb.append("\n  <td>").append(value == null ? "" : value).append("</td>");
			sb.append("\n</tr>");
			return sb.toString();
		}
	}

}