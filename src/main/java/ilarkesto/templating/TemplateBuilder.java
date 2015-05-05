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

import ilarkesto.core.logging.Log;

import java.util.Stack;

public class TemplateBuilder {

	private static Log log = Log.get(TemplateBuilder.class);

	private Template template;
	private Stack<ContainerElement> containerStack;

	public TemplateBuilder() {
		template = new Template();
		containerStack = new Stack<ContainerElement>();
		containerStack.push(template);
	}

	public IncludeElement include(String path) {
		return add(new IncludeElement(path.trim()));
	}

	public VariableElement variable(String expression) {
		return add(new VariableElement(expression));
	}

	public LoopElement startLoop(String expression) {
		return startContainer(new LoopElement(expression));
	}

	public ComponentIncludeElement startComponentInclude(String componentTemplatePath) {
		return startContainer(new ComponentIncludeElement(componentTemplatePath));
	}

	public <C extends ContainerElement> C startContainer(C container) {
		add(container);
		containerStack.push(container);
		return container;
	}

	public void endContainer() {
		if (isStackEmpty()) return;
		containerStack.pop();
	}

	public void text(String text) {
		if (text == null) return;
		if (text.isEmpty()) return;

		ContainerElement container = containerStack.peek();
		ATemplateElement last = container.getLast();
		if (last != null && last instanceof TextElement) {
			((TextElement) last).append(text);
			return;
		}

		container.add(new TextElement(text));
	}

	public final <E extends ATemplateElement> E add(E element) {
		containerStack.peek().add(element);
		return element;
	}

	public Template getTemplate() {
		// if (!isStackEmpty()) log.warn("Stack not empty, template is not correct.");
		return template;
	}

	public boolean isStackEmpty() {
		return containerStack.size() <= 1;
	}

}
