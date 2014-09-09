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
package ilarkesto.integration.infodoc;

import ilarkesto.core.base.Str;

public class Header extends AInfoDocElement {

	private String text;
	private int depth;

	public Header(String text, int depth) {
		super();
		this.text = text;
		this.depth = depth;
	}

	@Override
	public String toHtml(AHtmlContext context, AReferenceResolver referenceResolver) {
		StringBuilder sb = new StringBuilder();
		String ref = getRef();
		sb.append("\n<p style='" + context.getElementDepthStyle(getDepth()) + " color:" + context.getColor(getDepth())
				+ ";'>");
		if (ref == null) {
			sb.append(Str.toHtml(text, true));
		} else {
			String title = referenceResolver.getTitle(ref);
			String href = context.getHref(ref);
			sb.append("&nbsp;<a href='").append(href).append("' style='color:" + context.getColor(getDepth()) + ";'>")
					.append(Str.toHtml(title, true)).append("</a>");
		}
		sb.append("</p>\n");
		return sb.toString();
	}

	@Override
	AInfoDocElement setHeader(Header parent) {
		while (parent != null && depth < parent.depth) {
			parent = parent.getHeader();
		}
		if (parent == null) return super.setHeader(parent);
		if (depth == parent.depth) return super.setHeader(parent.getHeader());
		return super.setHeader(parent);
	}

	public boolean isRef() {
		return text.trim().startsWith("@");
	}

	public String getRef() {
		String ref = text.trim();
		if (!ref.startsWith("@")) return null;
		return ref.substring(1);
	}

	public String getText() {
		return text;
	}

	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public String toString() {
		return getText();
	}

}
