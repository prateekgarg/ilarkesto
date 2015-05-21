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
package ilarkesto.templating;

import java.util.ArrayList;
import java.util.List;

public class ContainerElement extends ATemplateElement {

	protected List<ATemplateElement> children = new ArrayList<ATemplateElement>();

	@Override
	public void onProcess() {
		processChildren();
	}

	protected void processChildren() {
		for (ATemplateElement child : children) {
			child.process(context);
		}
	}

	public ContainerElement add(ATemplateElement child) {
		if (child == null) return this;
		children.add(child);
		return this;
	}

	ATemplateElement getLast() {
		if (children.isEmpty()) return null;
		return children.get(children.size() - 1);
	}

	List<ATemplateElement> getChildren() {
		return children;
	}

}
