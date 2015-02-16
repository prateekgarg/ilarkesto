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

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class ASvgElement {

	protected abstract void provideAttributes(Map<String, String> attributes);

	protected abstract Point bottomRight();

	@Override
	public String toString() {
		return toString(0);
	}

	private String toString(int indent) {
		StringBuilder sb = new StringBuilder();
		appendNl(sb, indent);

		sb.append("<").append(getName()).append(" ");
		Map<String, String> attributes = new LinkedHashMap<String, String>();
		provideAttributes(attributes);
		for (Map.Entry<String, String> entry : attributes.entrySet()) {
			sb.append(entry.getKey()).append("=\"").append(entry.getValue()).append("\" ");
		}

		if (this instanceof ASvgContainer) {
			sb.append(">");
			ASvgContainer container = (ASvgContainer) this;
			for (ASvgElement child : container.getChildren()) {
				sb.append(child.toString(indent + 1));
			}
			appendNl(sb, indent);
			sb.append("</").append(getName()).append(">");
		} else {
			sb.append(" />");
		}
		return sb.toString();
	}

	private void appendNl(StringBuilder sb, int indent) {
		sb.append("\n");
		for (int i = 0; i < indent; i++) {
			sb.append("  ");
		}
	}

	public String getName() {
		return getClass().getSimpleName().toLowerCase();
	}

}
