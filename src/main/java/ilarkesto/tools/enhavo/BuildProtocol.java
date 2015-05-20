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
package ilarkesto.tools.enhavo;

import ilarkesto.core.base.Str;
import ilarkesto.core.logging.Log;
import ilarkesto.ui.web.HtmlBuilder;

import java.util.Stack;

public class BuildProtocol {

	private static Log log = Log.get(BuildProtocol.class);

	private Stack<String> contextStack = new Stack<String>();

	private HtmlBuilder html;

	public BuildProtocol(HtmlBuilder html) {
		super();
		this.html = html;
	}

	public void pushContext(String context) {
		info(context);
		contextStack.push(context);
		html.startDIV().setStyle("margin-left: 20px");
		html.flush();
	}

	public void popContext() {
		contextStack.pop();
		html.endDIV();
		html.flush();
	}

	public void info(Object... message) {
		StringBuilder sb = new StringBuilder();
		indent(sb, "");
		sb.append(Str.formatMessage(message));
		System.out.println(sb.toString());

		html.startDIV();
		html.text(Str.formatMessage(message));
		html.endDIV();
		html.flush();
	}

	public void error(Object... message) {
		StringBuilder sb = new StringBuilder();
		indent(sb, "!");
		sb.append(Str.formatMessage(message));
		System.out.println(sb.toString());

		html.startDIV().setStyle("color: red");
		html.text(Str.formatMessage(message));
		html.endDIV();
		html.flush();
	}

	private void indent(StringBuilder sb, String prefix) {
		sb.append(prefix);
		for (String context : contextStack) {
			sb.append("  ");
		}
	}

}
