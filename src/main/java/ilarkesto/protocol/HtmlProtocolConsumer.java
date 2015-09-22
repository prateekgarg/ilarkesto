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
import ilarkesto.ui.web.HtmlBuilder;
import ilarkesto.ui.web.HtmlBuilder.Tag;

import java.util.List;

public class HtmlProtocolConsumer extends AProtocolConsumer {

	private HtmlBuilder html;

	public HtmlProtocolConsumer(HtmlBuilder html) {
		super();
		this.html = html;
	}

	@Override
	public void onContextStart(String context, List<String> currentContext) {
		onMessage(new Message(Type.INFO, context), currentContext);
		html.startDIV().setStyle("margin-left: 20px");
		html.flush();
	}

	@Override
	public void onContextEnd(String context, List<String> currentContext) {
		html.endDIV();
		html.flush();
	}

	@Override
	public void onMessage(Message message, List<String> currentContext) {
		Tag div = html.startDIV();
		if (message.isImportant()) div.setStyle("color: red");
		html.text(message.getText());
		html.endDIV();
		html.flush();
	}

}
