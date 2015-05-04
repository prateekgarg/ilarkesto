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

public abstract class ATemplateElement {

	protected Context context;

	public abstract void onProcess();

	public final Context process(Context context) {
		this.context = context;
		onProcess();
		return context;
	}

	protected String format(Object o) {
		return context.getTextFormater().format(o);
	}

	protected String escape(Object text) {
		return context.getTextEscaper().escape(text.toString());
	}

	protected Object evalExpression(String expression) {
		return context.getExpressionProcessor().eval(expression, context);
	}

	protected boolean evalExpressionAsBoolean(String expression) {
		return isTrue(evalExpression(expression));
	}

	protected boolean isTrue(Object value) {
		if (value == null) return false;

		if (value instanceof Boolean) return ((Boolean) value).booleanValue();

		if (value instanceof Collection) return !((Collection) value).isEmpty();

		String s = value.toString().trim().toLowerCase();
		if (s.isEmpty()) return false;
		if (s.equals("false")) return false;
		if (s.equals("n")) return false;
		if (s.equals("no")) return false;
		if (s.equals("nein")) return false;
		if (s.equals("off")) return false;
		if (s.equals("0")) return false;
		return true;
	}

	protected Collection toCollection(Object value) {
		if (value == null) return Collections.emptyList();
		if (value instanceof Collection) return (Collection) value;
		if (value instanceof Iterable) return Utl.toList((Iterable) value);
		if (isTrue(value)) return Arrays.asList(value);
		return Collections.emptyList();
	}

	protected void print(Object value) {
		context.print(value);
	}

}