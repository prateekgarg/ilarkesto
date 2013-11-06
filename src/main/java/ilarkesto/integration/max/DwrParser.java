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
import ilarkesto.core.logging.Log;
import ilarkesto.integration.max.state.MaxCubeState;
import ilarkesto.json.Json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DwrParser extends Parser {

	private static Log log = Log.get(DwrParser.class);

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
					throw new MaxProtocolException("Callback variable does not exist: " + varName, data);
				return object;
			} else {
				throw new MaxProtocolException("Unsupported statement: " + statement, data);
			}
		}
		throw new MaxProtocolException("Missing callback statement", data);
	}

	private void parseValueAssignment(String statement, Map<String, Object> objects) {
		int dotIdx = statement.indexOf('.');
		int eqIdx = statement.indexOf('=');
		String name = statement.substring(0, dotIdx);

		Object object = objects.get(name);
		if (object == null) throw new MaxProtocolException("Variable not defined: " + name, data);

		String property = statement.substring(dotIdx + 1, eqIdx);
		String value = statement.substring(eqIdx + 1);

		Object valueObject = parseAssignementValue(value, objects);

		try {
			Reflect.setFieldValue(object, property, valueObject);
		} catch (Exception ex) {
			log.error("Setting property <" + property + "> with value " + valueObject.getClass().getSimpleName() + ":<"
					+ valueObject + "> on object <" + object.getClass().getSimpleName() + "> failed:", ex);
		}
	}

	private void parseArrayValueAssignment(String statement, Map<String, Object> objects) {
		int dotIdx = statement.indexOf('[');
		int eqIdx = statement.indexOf('=');
		String name = statement.substring(0, dotIdx);

		Collection object = (Collection) objects.get(name);
		if (object == null) throw new MaxProtocolException("Variable not defined: " + name, data);

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
			if (valueObject == null) throw new MaxProtocolException("Variable not defined: " + value, data);
			return valueObject;
		}

		if (value.startsWith("new Date(")) {
			String sMillis = value.substring(9, value.indexOf(')'));
			long millis = Long.parseLong(sMillis);
			return new Date(millis);
		}

		if (value.startsWith("\'") || value.startsWith("\""))
			return Json.parseString(value.substring(1, value.length() - 1));

		if (isInteger(value)) return Integer.parseInt(value);

		if (isFloat(value)) return Float.parseFloat(value);

		throw new MaxProtocolException("Unsupported variable assignment value: " + value, data);
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
			throw new MaxProtocolException("Unsupported variable value: " + value, data);
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
					sb.append(" -> ");
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
