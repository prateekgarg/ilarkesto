package ilarkesto.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Json {

	public static String valueToString(Object value, int indentation) {
		if (value == null) return "null";
		if (value instanceof String) return '"' + escapeString((String) value) + '"';
		if (value instanceof Iterable) {
			StringBuilder sb = new StringBuilder();
			sb.append('[');
			if (indentation >= 0) indentation++;
			boolean indentArray = indentation > 0 && !isShort((Iterable) value);
			Iterable list = (Iterable) value;
			boolean first = true;
			for (Object element : list) {
				if (first) {
					first = false;
				} else {
					sb.append(',');
				}
				if (indentArray) {
					sb.append('\n');
					indent(sb, indentation);
				}
				sb.append(valueToString(element, indentation));
			}
			if (indentArray) {
				sb.append('\n');
			}
			if (indentation >= 0) indentation--;
			indent(sb, indentation);
			sb.append(']');
			return sb.toString();
		}
		if (value instanceof JsonObject) return ((JsonObject) value).toString(indentation);
		if (value instanceof JsonWrapper) return ((JsonWrapper) value).getJson().toString(indentation);
		return value.toString();
	}

	static boolean isShort(Iterable iterable) {
		int size = 0;
		for (Object element : iterable) {
			if (element == null) continue;
			if (!isPrimitive(element)) return false;
			size += element.toString().length();
			if (size > 80) return false;
		}
		return true;
	}

	static boolean isPrimitive(Object value) {
		if (value == null) return true;
		if (value instanceof String) return true;
		if (value instanceof Number) return true;
		if (value instanceof Boolean) return true;
		return false;
	}

	static void indent(StringBuilder sb, int indentation) {
		for (int i = 0; i < indentation; i++) {
			sb.append('\t');
		}
	}

	public static String escapeString(String s) {
		if (s == null) return "";
		s = s.replace("\\", "\\\\");
		s = s.replace("\"", "\\\"");
		s = s.replace("\t", "\\t");
		s = s.replace("\r", "\\r");
		s = s.replace("\n", "\\n");
		s = s.replace("/", "\\/");
		return s;
	}

	public static String parseString(String s) {
		int idx = s.indexOf("\\u");
		while (idx >= 0) {
			String code = s.substring(idx + 2, idx + 6);
			char ch = (char) Integer.parseInt(code, 16);
			s = s.replace("\\u" + code, String.valueOf(ch));
			idx = s.indexOf("\\u", idx);
		}
		s = s.replace("\\\\", "\\");
		s = s.replace("\\\"", "\"");
		s = s.replace("\\t", "\t");
		s = s.replace("\\r", "\r");
		s = s.replace("\\n", "\n");
		s = s.replace("\\/", "/");
		return s;
	}

	public static Number parseNumber(String s) throws NumberFormatException {
		if (s.contains(".")) return Double.parseDouble(s);
		return Long.parseLong(s);
	}

	public static boolean isWhitespace(char c) {
		return c == ' ' || c == '\t' || c == '\n' || c == '\r';
	}

	static Object convertValue(Object value) {
		if (value == null) return null;
		if (value instanceof JsonWrapper) return ((JsonWrapper) value).getJson();
		if (value instanceof JsonObject) return value;
		if (value instanceof String) return value;
		if (value instanceof List) return value;
		if (value instanceof Boolean) return value;
		if (value instanceof Number) return value;
		if (value instanceof Collection) return new ArrayList((Collection) value);
		if (value instanceof Iterable) {
			List ret = new ArrayList();
			for (Object element : (Iterable) value) {
				ret.add(element);
			}
			return ret;
		}
		if (value instanceof Map) return new JsonObject((Map) value);
		return value.toString();
	}

	static int getFirstNonWhitespaceIndex(String s, int offset) {
		int len = s.length();
		for (int i = offset; i < len; i++) {
			if (!isWhitespace(s.charAt(i))) return i;
		}
		return -1;
	}

	static int getFirstQuoting(String s, int offset) {
		int len = s.length();
		for (int i = offset; i < len; i++) {
			char ch = s.charAt(i);
			if (ch == '\\') {
				i++;
				continue;
			}
			if (ch == '"') return i;
		}
		return -1;
	}

	public static interface JsonWrapper {

		JsonObject getJson();
	}

}
