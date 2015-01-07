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

public class Reference extends AInfoDocElement {

	private String ref;

	public Reference(InfoDocStructure structure, String ref) {
		super(structure);
		this.ref = ref;
	}

	@Override
	public String toHtml(AHtmlContext context, AReferenceResolver referenceResolver) {
		String title = referenceResolver.getTitle(ref);
		if (Str.isBlank(title)) title = "@" + ref;
		String href = context.getHref(ref);
		StringBuilder sb = new StringBuilder();
		sb.append(
			"\n<p style='" + context.getElementDepthStyle(getDepth()) + " color:" + context.getColor(getDepth()) + "'>")
				.append("<a href='").append(href).append("' style='color:" + context.getColor(getDepth()) + "'>")
				.append(Str.toHtml(title, true)).append("</a></p>\n");
		return sb.toString();
	}

	public String getRef() {
		return ref;
	}

	@Override
	public String toString() {
		return getRef();
	}

}
