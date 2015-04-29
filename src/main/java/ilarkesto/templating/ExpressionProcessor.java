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

import ilarkesto.base.Reflect;
import ilarkesto.base.Str;
import ilarkesto.core.logging.Log;

import java.util.Map;

public class ExpressionProcessor {

	private Log log = Log.get(ExpressionProcessor.class);

	public Object eval(String expression, Context context) {
		if (Str.isBlank(expression)) return null;
		if (expression.startsWith("/")) return evalOnObject(expression.substring(1), context.getRootScope());
		if (expression.startsWith("#")) return evalOnObject(expression, context.getRootScope());
		return evalOnObject(expression, context.getScope());
	}

	private Object evalOnObject(String expression, Object object) {
		if (object == null) return null;

		int idx = expression.indexOf('/');
		if (idx <= 0) return getFromObject(expression, object);

		object = getFromObject(expression.substring(0, idx), object);
		if (object == null) return null;
		return evalOnObject(expression.substring(idx + 1), object);
	}

	private Object getFromObject(String propertyExpression, Object object) {
		String property = propertyExpression;
		if (object instanceof Map) return ((Map) object).get(property);
		try {
			return Reflect.getProperty(object, property);
		} catch (Throwable ex) {
			log.debug("getFromObject() failed:", ex);
			return null;
		}
	}

}
