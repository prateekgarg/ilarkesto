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

import ilarkesto.base.Utl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class LoopElement implements TemplateElement {

	private String expression;
	private TemplateElement contentTemplate;

	private String name = "loop";
	private boolean changeScopeToItem = true;

	public LoopElement(String expression, TemplateElement contentTemplate) {
		super();
		this.expression = expression;
		this.contentTemplate = contentTemplate;
	}

	@Override
	public Context process(Context context) {

		Object value = context.getExpressionProcessor().eval(expression, context);

		Collection items = toCollection(value);
		if (items == null || items.isEmpty()) return context;

		Map<String, Object> loopProperties = new HashMap<String, Object>();
		String loopPropertiesName = "#" + name;
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

		return context;
	}

	private Collection toCollection(Object value) {
		if (value == null) return Collections.emptyList();
		if (value instanceof Collection) return (Collection) value;
		if (value instanceof Iterable) return Utl.toList((Iterable) value);
		return Arrays.asList(value);
	}

	private void process(Context context, Object item) {
		Object oldScope = context.getScope();
		if (changeScopeToItem) context.setScope(item);

		contentTemplate.process(context);

		if (changeScopeToItem) context.setScope(oldScope);
	}

}