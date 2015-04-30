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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class LoopElement extends ContainerElement {

	private String expression;

	private String name = "loop";

	public LoopElement(String expression) {
		this.expression = expression;
	}

	public LoopElement(String expression, ATemplateElement contentTemplate) {
		this(expression);
		add(contentTemplate);
	}

	@Override
	public void onProcess() {
		Object value = evalExpression(expression);

		Collection items = toCollection(value);
		if (items == null || items.isEmpty()) return;

		Map<String, Object> loopProperties = new HashMap<String, Object>();
		String loopPropertiesName = "$" + name;
		context.put(loopPropertiesName, loopProperties);

		int count = items.size();
		loopProperties.put("count", count);
		int index = 0;
		int position = 1;
		loopProperties.put("last", false);
		for (Object item : items) {
			loopProperties.put("item", item);
			loopProperties.put("index", index);
			loopProperties.put("position", position);
			if (index == 0) loopProperties.put("first", true);
			if (index == 1) loopProperties.put("first", false);
			if (index == count - 1) loopProperties.put("last", true);
			process(context, item);
			index++;
			position++;
		}

		context.remove(loopPropertiesName);
	}

	private void process(Context context, Object item) {
		boolean changeScopeToItem = !isPrimitive(item);

		Object oldScope = context.getScope();
		if (changeScopeToItem) context.setScope(item);

		processChildren();

		if (changeScopeToItem) context.setScope(oldScope);
	}

	private boolean isPrimitive(Object o) {
		if (o instanceof String) return true;
		if (o instanceof Boolean) return true;
		if (o instanceof Number) return true;
		return false;
	}

	@Override
	public LoopElement add(ATemplateElement element) {
		super.add(element);
		return this;
	}

	public String getExpression() {
		return expression;
	}

}