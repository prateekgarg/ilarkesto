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
import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;
import ilarkesto.json.JsonSaxParser.ContentHandler;
import ilarkesto.json.JsonSaxParser.ParseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class JsonMapper {

	private static Log log = Log.get(JsonMapper.class);

	public static <T> T deserialize(File file, Class<T> type, TypeResolver typeResolver) throws IOException,
			ParseException {
		BufferedReader in = new BufferedReader(new FileReader(file));
		T ret = deserialize(in, type, typeResolver);
		IO.close(in);
		return ret;
	}

	public static <T> T deserialize(String s, Class<T> type, TypeResolver typeResolver) throws IOException,
			ParseException {
		JsonSaxParser parser = new JsonSaxParser();
		ObjectMappingContentHandler<T> handler = new ObjectMappingContentHandler<T>(type, typeResolver);
		parser.parse(s, handler);
		return handler.getObject();
	}

	public static <T> T deserialize(Reader in, Class<T> type, TypeResolver typeResolver) throws IOException,
			ParseException {
		JsonSaxParser parser = new JsonSaxParser();
		ObjectMappingContentHandler<T> handler = new ObjectMappingContentHandler<T>(type, typeResolver);
		parser.parse(in, handler);
		return handler.getObject();
	}

	public static void serialize(Object object, PrintWriter out) {
		serialize(object, out, 0);
	}

	public static void serialize(Object object, PrintWriter out, int indent) {
		if (object == null || object instanceof String || object instanceof Number || object instanceof Boolean) {
			Json.printValue(object, out, 0);
			return;
		}

		// TODO array handling

		if (object instanceof Iterable) {
			out.print("[ ");
			boolean first = true;
			for (Object item : ((Iterable) object)) {
				if (first) {
					first = false;
				} else {
					out.print(", ");
				}
				serialize(item, out, indent);
			}
			out.print(" ]");
			return;
		}

		out.print('{');
		indent++;
		boolean first = true;
		for (Field field : Reflect.getSerializableFields(object)) {
			if (first) {
				first = false;
			} else {
				out.print(',');
			}
			nlindent(out, indent);
			out.print('\"');
			out.print(Json.escapeString(field.getName()));
			out.print("\": ");
			Object fieldValue;
			if (!field.isAccessible()) field.setAccessible(true);
			try {
				fieldValue = field.get(object);
			} catch (Exception ex) {
				throw new RuntimeException("Reading field value from " + field.getClass() + "." + field.getName()
						+ " failed.", ex);
			}
			serialize(fieldValue, out, indent);
		}
		indent--;
		nlindent(out, indent);
		out.print('}');
	}

	private static void nlindent(PrintWriter out, int indent) {
		out.print('\n');
		indent(out, indent);
	}

	private static void indent(PrintWriter out, int indent) {
		for (int i = 0; i < indent; i++)
			out.print(' ');
	}

	public static void serialize(Object object, File file) throws IOException {
		serialize(object, file, true);
	}

	public static void serialize(Object object, File file, boolean wirteToTemporaryFileFirst) throws IOException {
		if (file == null) throw new IllegalArgumentException("file == null");
		file.getParentFile().mkdirs();

		File tempFile = new File(file.getPath() + ".tmp");
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(tempFile)));
		serialize(object, out);
		out.close();

		try {
			IO.delete(file);
			IO.move(tempFile, file);
		} finally {
			IO.delete(tempFile);
		}
	}

	public static String serialize(Object object) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		serialize(object, out);
		out.close();
		return stringWriter.toString();
	}

	public static abstract class TypeResolver {

		public Class resolveType(Object object, String field) {
			return Reflect.getDeclaredField(object.getClass(), field).getType();
		}

		public abstract Class resolveArrayType(Object object, String field);

	}

	private static class ObjectMappingContentHandler<T> implements ContentHandler {

		private Class<T> objectType;
		private T object;
		private String currentAttributeName;
		private List currentArray;
		private ObjectMappingContentHandler subHandler;
		private ObjectMappingContentHandler parent;
		private TypeResolver typeResolver;

		public ObjectMappingContentHandler(Class<T> type, TypeResolver typeResolver) {
			super();
			this.objectType = type;
			this.typeResolver = typeResolver;
		}

		@Override
		public void onBegin() throws ParseException, IOException {}

		@Override
		public boolean onBeginObject() throws ParseException, IOException {
			// log.info(objectType, "------->", "onBeginObject");
			if (subHandler != null) {
				subHandler.onBeginObject();
				return true;
			}
			if (object == null) {
				// log.debug("Instantiating:", objectType);
				object = Reflect.newInstance(objectType);
				return true;
			}
			Class resolvedType = currentArray == null ? Reflect.getDeclaredField(objectType, currentAttributeName)
					.getType() : typeResolver.resolveArrayType(object, currentAttributeName);
			if (resolvedType == null)
				throw new IllegalStateException("Type could not be resolved: " + objectType.getSimpleName() + "."
						+ currentAttributeName);
			subHandler = new ObjectMappingContentHandler(resolvedType, typeResolver);
			subHandler.parent = this;
			subHandler.onBeginObject();
			return true;
		}

		@Override
		public boolean onEndObject() throws ParseException, IOException {
			// log.info(objectType, "------->", "onEndObject");
			if (subHandler != null) {
				subHandler.onEndObject();
				return true;
			}
			if (parent != null) {
				// log.info(objectType, "------->", "ending current object in parent");
				if (parent.currentArray != null) {
					parent.currentArray.add(getObject());
				} else {
					Reflect.setFieldValue(parent.object, parent.currentAttributeName, getObject());
				}
				parent.subHandler = null;
				return true;
			}
			return true;
		}

		@Override
		public boolean onBeginAttribute(String attributeName) throws ParseException, IOException {
			// log.info(objectType, "------->", "onBeginAttribute", attributeName);
			if (subHandler != null) {
				subHandler.onBeginAttribute(attributeName);
				return true;
			}
			currentAttributeName = attributeName;
			return true;
		}

		@Override
		public boolean onEndAttribute() throws ParseException, IOException {
			// log.info(objectType, "------->", "onEndAttribute");
			if (subHandler != null) {
				subHandler.onEndAttribute();
				return true;
			}
			currentAttributeName = null;
			return true;
		}

		@Override
		public boolean onBeginArray() throws ParseException, IOException {
			// log.info(objectType, "------->", "onBeginArray");
			if (subHandler != null) {
				subHandler.onBeginArray();
				return true;
			}

			Field field = Reflect.getDeclaredField(object.getClass(), currentAttributeName);
			Class<?> fieldType = field.getType();
			if (fieldType.isAssignableFrom(List.class)) {
				currentArray = new ArrayList();
			} else {
				throw new IllegalStateException("Unsupported collection type: " + fieldType.getName());
			}
			Reflect.setFieldValue(object, currentAttributeName, currentArray);
			return true;
		}

		@Override
		public boolean onEndArray() throws ParseException, IOException {
			// log.info(objectType, "------->", "onEndArray");
			if (subHandler != null) {
				subHandler.onEndArray();
				return true;
			}

			currentArray = null;
			return true;
		}

		@Override
		public boolean onPrimitiveValue(Object value) throws ParseException, IOException {
			// log.info(objectType, "------->", "onPrimitiveValue", value);
			if (subHandler != null) {
				subHandler.onPrimitiveValue(value);
				return true;
			}

			if (currentArray != null) {
				currentArray.add(value);
				return true;
			}
			Reflect.setFieldValue(object, currentAttributeName, value);
			return true;
		}

		@Override
		public void onEnd() throws ParseException, IOException {}

		public T getObject() {
			return object;
		}

	}
}
