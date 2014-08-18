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

public class ReferenceHeader extends AInfoDocElement {

	private String ref;
	private int depth;

	public ReferenceHeader(String ref, int depth) {
		super();
		this.ref = ref;
		this.depth = depth;
	}

	@Override
	public String toHtml(AHtmlContext context, AReferenceResolver referenceResolver) {
		String title = referenceResolver.getTitle(ref);
		String href = context.getHref(ref);
		StringBuilder sb = new StringBuilder();
		sb.append("\n<h" + depth + ">").append(Str.toHtml(title, true));
		sb.append("&nbsp;<a href='").append(href).append("'>").append("-&gt;").append("</a></h" + depth + ">\n");
		return sb.toString();
	}

	public String getRef() {
		return ref;
	}

	public int getDepth() {
		return depth;
	}

	@Override
	public String toString() {
		return getRef();
	}
}
