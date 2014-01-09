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
package ilarkesto.json;

import ilarkesto.base.Reflect;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

public class JsonMapper {

	public static void serialize(Object object, PrintWriter out) {
		if (object == null || object instanceof String || object instanceof Number || object instanceof Boolean) {
			Json.printValue(object, out, 0);
			return;
		}

		// TODO array handling

		if (object instanceof Iterable) {
			out.print('[');
			boolean first = true;
			for (Object item : ((Iterable) object)) {
				if (first) {
					first = false;
				} else {
					out.print(',');
				}
				serialize(item, out);
			}
			out.print(']');
			return;
		}

		out.print('{');
		boolean first = true;
		for (Field field : Reflect.getSerializableFields(object)) {
			if (first) {
				first = false;
			} else {
				out.print(',');
			}
			out.print('\"');
			out.print(Json.escapeString(field.getName()));
			out.print("\":");
			Object fieldValue;
			if (!field.isAccessible()) field.setAccessible(true);
			try {
				fieldValue = field.get(object);
			} catch (Exception ex) {
				throw new RuntimeException("Reading field value from " + field.getClass() + "." + field.getName()
						+ " failed.", ex);
			}
			serialize(fieldValue, out);
		}
		out.print('}');
	}

	public static String serialize(Object object) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		serialize(object, out);
		out.close();
		return stringWriter.toString();
	}
}
