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

import ilarkesto.protocol.Message.Type;

import java.util.List;

public class SysoutProtocolConsumer extends AProtocolConsumer {

	@Override
	public void onContextStart(String context, List<String> currentContext) {
		super.onContextStart(context, currentContext);
		onMessage(new Message(Type.INFO, context), currentContext);
	}

	@Override
	public void onMessage(Message message, List<String> currentContext) {
		StringBuilder sb = new StringBuilder();
		indent(sb, message.isImportant() ? "!" : "", currentContext);
		sb.append(message.getText());
		System.out.println(sb.toString());
	}

	private void indent(StringBuilder sb, String prefix, List<String> currentContext) {
		sb.append(prefix);
		for (String context : currentContext) {
			sb.append("  ");
		}
	}

}
