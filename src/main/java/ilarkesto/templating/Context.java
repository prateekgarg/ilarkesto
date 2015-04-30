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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public class Context {

	private ExpressionProcessor expressionProcessor = new ExpressionProcessor();

	private StringWriter stringWriter;
	private PrintWriter out;

	private Map<String, Object> rootScope = new LinkedHashMap<String, Object>();
	private Object scope = rootScope;

	public Context(PrintWriter out) {
		super();
		this.out = out;
	}

	public Context() {
		stringWriter = new StringWriter();
		out = new PrintWriter(stringWriter);
	}

	public void put(String name, Object value) {
		rootScope.put(name, value);
	}

	public void remove(String name) {
		rootScope.remove(name);
	}

	public void setScope(Object scope) {
		this.scope = scope;
	}

	public Object getScope() {
		return scope;
	}

	Map<String, Object> getRootScope() {
		return rootScope;
	}

	void print(Object text) {
		out.print(text);
	}

	public void setExpressionProcessor(ExpressionProcessor resolver) {
		this.expressionProcessor = resolver;
	}

	public ExpressionProcessor getExpressionProcessor() {
		return expressionProcessor;
	}

	public String popOutput() {
		if (stringWriter == null)
			throw new IllegalStateException("Context was constructed with PrintWriter, peekOutput() not allowed");
		out.close();
		String ret = stringWriter.toString();
		stringWriter = new StringWriter();
		out = new PrintWriter(stringWriter);
		return ret;
	}

}
