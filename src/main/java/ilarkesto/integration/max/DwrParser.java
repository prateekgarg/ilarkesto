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
package ilarkesto.integration.max;

import ilarkesto.base.Reflect;
import ilarkesto.core.base.Parser;
import ilarkesto.core.base.Utl;
import ilarkesto.integration.max.state.MaxCubeState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DwrParser extends Parser {

	public DwrParser(String data) {
		super(data);
	}

	public Object parseCallbackObject() {
		Map<String, Object> objects = new HashMap<String, Object>();
		String statement;
		while ((statement = getNextStatement()) != null) {
			if (statement.startsWith("//")) continue;
			if (statement.length() == 0) continue;
			if (statement.startsWith("throw ")) continue;
			if (statement.startsWith("var ")) {
				parseVarStatement(statement, objects);
			} else if (statement.startsWith("s") && statement.contains(".") && statement.contains("=")) {
				parseValueAssignment(statement, objects);
			} else if (statement.startsWith("s") && statement.contains("[") && statement.contains("]")
					&& statement.contains("=")) {
				parseArrayValueAssignment(statement, objects);
			} else if (statement.startsWith("dwr.engine._remoteHandleCallback(")) {
				int idx = statement.lastIndexOf(',');
				String varName = statement.substring(idx + 1, statement.lastIndexOf(')'));
				Object object = objects.get(varName);
				if (object == null)
					throw new MaxPortalProtocolException("Callback variable does not exist: " + varName);
				return object;
			} else {
				throw new MaxPortalProtocolException("Unsupported statement: " + statement);
			}
		}
		throw new MaxPortalProtocolException("Missing callback statement");
	}

	private void parseValueAssignment(String statement, Map<String, Object> objects) {
		int dotIdx = statement.indexOf('.');
		int eqIdx = statement.indexOf('=');
		String name = statement.substring(0, dotIdx);

		Object object = objects.get(name);
		if (object == null) throw new MaxPortalProtocolException("Variable not defined: " + name);

		String property = statement.substring(dotIdx + 1, eqIdx);
		String value = statement.substring(eqIdx + 1);

		Object valueObject = parseAssignementValue(value, objects);

		try {
			Reflect.setFieldValue(object, property, valueObject);
		} catch (Throwable ex) {
			System.err.println("Setting property <" + property + "> on object <" + object.getClass().getSimpleName()
					+ "> failed: " + Utl.getRootCause(ex).getMessage());
		}
	}

	private void parseArrayValueAssignment(String statement, Map<String, Object> objects) {
		int dotIdx = statement.indexOf('[');
		int eqIdx = statement.indexOf('=');
		String name = statement.substring(0, dotIdx);

		Collection object = (Collection) objects.get(name);
		if (object == null) throw new MaxPortalProtocolException("Variable not defined: " + name);

		String value = statement.substring(eqIdx + 1);

		Object valueObject = parseAssignementValue(value, objects);

		object.add(valueObject);
	}

	private Object parseAssignementValue(String value, Map<String, Object> objects) {
		if (value.equals("true")) return true;

		if (value.equals("false")) return false;

		if (value.equals("null")) return null;

		if (value.startsWith("s") && value.length() <= 5) {
			Object valueObject = objects.get(value);
			if (valueObject == null) throw new MaxPortalProtocolException("Variable not defined: " + value);
			return valueObject;
		}

		if (value.startsWith("new Date(")) {
			String sMillis = value.substring(9, value.indexOf(')'));
			long millis = Long.parseLong(sMillis);
			return new Date(millis);
		}

		if (value.startsWith("\'") || value.startsWith("\"")) return value.substring(1, value.length() - 1);

		if (isInteger(value)) return Integer.parseInt(value);

		if (isFloat(value)) return Float.parseFloat(value);

		throw new MaxPortalProtocolException("Unsupported variable assignment value: " + value);
	}

	private void parseVarStatement(String statement, Map<String, Object> objects) {
		int idx = statement.indexOf('=');
		String name = statement.substring(4, idx);
		String value = statement.substring(idx + 1);
		Object object;
		if (value.equals("[]")) {
			object = new ArrayList();
		} else if (value.startsWith("new ")) {
			String className = value.substring(4, value.indexOf('('));
			object = Reflect.newInstance(MaxCubeState.class.getPackage().getName() + "." + className);
		} else {
			throw new MaxPortalProtocolException("Unsupported variable value: " + value);
		}
		objects.put(name, object);
	}

	private boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (Throwable ex) {
			return false;
		}
		return true;
	}

	private boolean isFloat(String s) {
		try {
			Float.parseFloat(s);
		} catch (Throwable ex) {
			return false;
		}
		return true;
	}

	private String getNextStatement() {
		if (isEnd()) return null;
		String statement = getUntilIf(";", "\n");
		if (statement == null) statement = tail();
		skip(statement.length() + 1);
		return statement.trim();
	}

	public String getErrorMessage() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		while (gotoAfterIf("message=\"")) {
			String s = getUntilIf("\"");
			if (s != null && s.trim().length() > 0) {
				if (first) {
					first = false;
				} else {
					sb.append("-> ");
				}
				sb.append(s);
			}
		}
		return sb.toString();
	}

	public boolean isError() {
		return contains("dwr.engine._remoteHandleException(");
	}

}
