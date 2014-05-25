/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>, Artjom Kochtchi
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.core.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Str {

	public static final char ue = '\u00FC';
	public static final char UE = '\u00DC';
	public static final char oe = '\u00F6';
	public static final char OE = '\u00D6';
	public static final char ae = '\u00E4';
	public static final char AE = '\u00C4';
	public static final char sz = '\u00DF';

	public static final char EUR = '\u0080';

	public static String formatWithThousandsSeparator(long value, String separator) {
		return formatWithThousandsSeparator(String.valueOf(value), separator);
	}

	public static String formatWithThousandsSeparator(String s, String separator) {
		if (s == null) return null;
		if (separator == null || s.length() <= 3) return s;
		boolean negative = false;
		if (s.startsWith("-")) {
			negative = true;
			s = s.substring(1);
		}
		if (s.length() > 3) s = s.substring(0, s.length() - 3) + separator + s.substring(s.length() - 3);
		if (s.length() > 7) s = s.substring(0, s.length() - 7) + separator + s.substring(s.length() - 7);
		if (s.length() > 11) s = s.substring(0, s.length() - 11) + separator + s.substring(s.length() - 11);
		if (negative) s = '-' + s;
		return s;
	}

	public static String trimAndNull(String s) {
		if (s == null) return null;
		s = s.trim();
		if (s.length() == 0) return null;
		return s;
	}

	public static List<String> parseCommaSeparatedString(String s) {
		List<String> result = new ArrayList<String>();
		if (s == null) return result;
		return Arrays.asList(s.split(","));
	}

	public static String toFileCompatibleString(String s) {
		s = s.replace('/', '-');
		s = s.replace('\\', '-');
		s = s.replace(':', '_');
		s = s.replace(';', '_');
		s = s.replace('&', '@');
		s = s.replace('?', '@');
		s = s.replace('=', '_');
		s = s.replace(String.valueOf(ae), "ae");
		s = s.replace(String.valueOf(AE), "Ae");
		s = s.replace(String.valueOf(ue), "ue");
		s = s.replace(String.valueOf(UE), "Ue");
		s = s.replace(String.valueOf(oe), "oe");
		s = s.replace(String.valueOf(OE), "Oe");
		s = s.replace(String.valueOf(sz), "ss");
		s = s.replace(String.valueOf(EUR), "EUR");
		return s;
	}

	public static boolean containsDigit(String s) {
		if (s == null) return false;
		for (int i = 0; i < s.length(); i++) {
			if (Character.isDigit(s.charAt(i))) return true;
		}
		return false;
	}

	public static boolean containsLetter(String s) {
		if (s == null) return false;
		for (int i = 0; i < s.length(); i++) {
			if (Character.isLetter(s.charAt(i))) return true;
		}
		return false;
	}

	public static boolean containsNonLetterOrDigit(String s) {
		if (s == null) return false;
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isLetterOrDigit(s.charAt(i))) return true;
		}
		return false;
	}

	public static String toUrl(String s) {
		if (s == null) return null;
		if (s.contains("://")) return s;
		if (s.startsWith("mailto:")) return s;
		return "http://" + s;
	}

	public static String formatUrlAsLink(String url) {
		return formatUrlAsLink(url, 50);
	}

	public static String formatUrlAsLink(String url, int maxLength) {
		if (url == null) return null;
		url = removePrefix(url, "http://");
		url = removePrefix(url, "https://");
		url = removePrefix(url, "ftp://");
		url = removePrefix(url, "mailto://");
		url = removePrefix(url, "apt://");
		url = removePrefix(url, "file://");
		url = removePrefix(url, "www.");
		int paramsIdx = url.indexOf('?');
		if (paramsIdx > 0) url = url.substring(0, paramsIdx);
		url = removeSuffix(url, "/");
		url = cutRight(url, maxLength, "...");
		return url;
	}

	public static String uppercaseFirstLetter(String s) {
		if (s.isEmpty()) throw new IllegalArgumentException("empty string");
		StringBuilder sb = new StringBuilder();
		sb.append(Character.toUpperCase(s.charAt(0)));
		sb.append(s.substring(1));
		return sb.toString();
	}

	public static String lowercaseFirstLetter(String s) {
		StringBuilder sb = new StringBuilder();
		sb.append(Character.toLowerCase(s.charAt(0)));
		sb.append(s.substring(1));
		return sb.toString();
	}

	public static String getFirstLine(String s) {
		return getFirstLine(s, Integer.MAX_VALUE, null);
	}

	public static String getFirstLine(String s, int cutAfterLength, String appendAfterCut) {
		return getLine(s, 0, cutAfterLength, appendAfterCut);
	}

	public static String getLine(String s, int index, int cutAfterLength, String appendAfterCut) {
		String ret = getLine(s, index);
		if (ret == null) return "";
		if (ret.length() > cutAfterLength) {
			ret = ret.substring(0, cutAfterLength);
			if (appendAfterCut != null) ret += appendAfterCut;
		}
		return ret;
	}

	public static String getLine(String s, int index) {
		if (s == null) return null;

		for (int i = 0; i < index; i++) {
			int idx = s.indexOf('\n');
			if (idx < 0) return null;
			s = s.substring(idx + 1);
		}

		int idx = indexOf(s, new String[] { "\r\n", "\n" }, 0);
		if (idx < 0) return s;
		return s.substring(0, idx);
	}

	public static String replaceIndexedParams(String s, Object... params) {
		if (s == null) return null;
		if (params == null || params.length == 0) return s;
		for (int i = 0; i < params.length; i++) {
			String param = format(params[i]);
			if (param == null) continue;
			s = s.replace("{" + i + "}", param);
		}
		return s;
	}

	public static String uppercase(CharSequence text) {
		if (text == null) return null;
		return String.valueOf(text).toUpperCase();
	}

	public static String fillUpLeft(String s, String filler, int minLength) {
		// TODO: optimize algorithm
		while (s.length() < minLength) {
			s = filler + s;
		}
		return s;
	}

	public static String cutLeft(String s, int maxlength, String fillerOnCut) {
		if (s == null) return null;
		if (s.length() > maxlength) {
			return fillerOnCut + s.substring(s.length() - maxlength + fillerOnCut.length());
		} else return s;
	}

	public static String cutRight(String s, int maxlength) {
		if (s == null) return null;
		if (s.length() > maxlength) {
			return s.substring(0, maxlength);
		} else return s;
	}

	public static String cutRight(String s, int maxlength, String fillerOnCut) {
		if (s == null) return null;
		if (s.length() > maxlength) {
			return s.substring(0, maxlength - fillerOnCut.length()) + fillerOnCut;
		} else return s;
	}

	public static String encodeUrlParameter(String s) {
		if (s == null) return "";
		StringBuilder sb = new StringBuilder();
		int len = s.length();
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			switch (c) {
				case '$':
					sb.append("%24");
					break;
				case '&':
					sb.append("%26");
					break;
				case '+':
					sb.append("%2B");
					break;
				case ',':
					sb.append("%2C");
					break;
				case '/':
					sb.append("%2F");
					break;
				case ':':
					sb.append("%3A");
					break;
				case ';':
					sb.append("%3B");
					break;
				case '=':
					sb.append("%3D");
					break;
				case '?':
					sb.append("%3F");
					break;
				case '@':
					sb.append("%40");
					break;
				case ' ':
					sb.append("%20");
					break;
				case '"':
					sb.append("%22");
					break;
				case '<':
					sb.append("%3C");
					break;
				case '>':
					sb.append("%3E");
					break;
				case '#':
					sb.append("%23");
					break;
				case '%':
					sb.append("%25");
					break;
				case '{':
					sb.append("7B%");
					break;
				case '}':
					sb.append("7D%");
					break;
				case '|':
					sb.append("%7C");
					break;
				case '\\':
					sb.append("%5C");
					break;
				case '^':
					sb.append("%5E");
					break;
				case '~':
					sb.append("%7E");
					break;
				case '[':
					sb.append("%5B");
					break;
				case ']':
					sb.append("%5D");
					break;
				case '`':
					sb.append("%60");
					break;
				default:
					sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String[] toStringArray(Collection<String> c) {
		return toStringArray(c.toArray());
	}

	public static String[] toStringArray(Object[] oa) {
		String[] sa = new String[oa.length];
		for (int i = 0; i < oa.length; i++) {
			sa[i] = oa[i] == null ? null : oa[i].toString();
		}
		return sa;
	}

	public static String toStringHelper(Object thiz, Object... properties) {
		return toStringHelper(getSimpleName(thiz.getClass()), properties);
	}

	public static String toStringHelper(String name, Object... properties) {
		return concat(name + "(", ")", ", ", properties);
	}

	public static String concat(String prefix, String suffix, String delimiter, Object... objects) {
		StringBuilder sb = new StringBuilder();
		if (prefix != null) sb.append(prefix);
		for (int i = 0; i < objects.length; i++) {
			sb.append(objects[i]);
			if (delimiter != null && i < objects.length - 1) {
				sb.append(delimiter);
			}
		}
		if (suffix != null) sb.append(suffix);
		return sb.toString();
	}

	public static String concat(Object[] sa, String delimiter) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < sa.length; i++) {
			sb.append(sa[i]);
			if (i < sa.length - 1) {
				sb.append(delimiter);
			}
		}
		return sb.toString();
	}

	public static String concat(Object[] sa) {
		return concat(sa, " ");
	}

	public static String concat(Iterable strings, String delimiter) {
		if (strings == null) return null;
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Object s : strings) {
			if (first) {
				first = false;
			} else {
				sb.append(delimiter);
			}
			sb.append(s);
		}
		return sb.toString();
	}

	public static String concat(Collection strings, String delimiter) {
		if (strings == null) return null;
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Object s : strings) {
			if (first) {
				first = false;
			} else {
				sb.append(delimiter);
			}
			sb.append(s);
		}
		return sb.toString();
	}

	/**
	 * Removes a suffix from a string, if it exists.
	 */
	public static String removeSuffix(String s, String suffixToRemove) {
		if (s == null) return null;
		if (!s.endsWith(suffixToRemove)) return s;
		return s.substring(0, s.length() - suffixToRemove.length());
	}

	public static String removeSuffixStartingWith(String s, String suffixIndicator) {
		if (s == null) return null;
		int idx = s.indexOf(suffixIndicator);
		if (idx < 0) return s;
		return s.substring(0, idx);
	}

	public static String removePrefix(String s, String prefixToRemove) {
		if (s == null) return null;
		if (!s.startsWith(prefixToRemove)) return s;
		return s.substring(prefixToRemove.length());
	}

	public static String getFirstParagraph(String s) {
		return getFirstParagraph(s, "\n\n");
	}

	public static String getFirstParagraph(String s, String paragraphEndIndicator) {
		if (s == null) return null;
		int idx = s.indexOf(paragraphEndIndicator);
		if (idx <= 0) return s;
		return s.substring(0, idx);
	}

	public static String appendIfNotBlank(String s, String suffix) {
		if (isBlank(s)) return s;
		return s + suffix;
	}

	public static String toHtml(String s) {
		if (s == null) return null;
		s = s.replace("&", "&amp;");
		s = s.replace(String.valueOf(ae), "&auml;");
		s = s.replace(String.valueOf(ue), "&uuml;");
		s = s.replace(String.valueOf(oe), "&ouml;");
		s = s.replace(String.valueOf(AE), "&Auml;");
		s = s.replace(String.valueOf(UE), "&Uuml;");
		s = s.replace(String.valueOf(OE), "&Ouml;");
		s = s.replace(String.valueOf(sz), "&szlig;");
		s = s.replace(String.valueOf(EUR), "&euro;");
		s = s.replace("<", "&lt;");
		s = s.replace(">", "&gt;");
		s = s.replace("\"", "&quot;");
		s = s.replace("\n", "<br>");
		return s;
	}

	public static String getLeadingSpaces(String s) {
		StringBuilder sb = new StringBuilder();
		int len = s.length();
		for (int i = 0; i < len; i++) {
			if (s.charAt(i) != ' ') break;
			sb.append(' ');
		}
		return sb.toString();
	}

	public static String removeCenters(String s, String from, String to) {
		if (s == null) return null;
		while (true) {
			int previousLength = s.length();
			s = removeCenter(s, from, to);
			if (previousLength == s.length()) return s;
		}
	}

	public static String removeCenter(String s, String from, String to) {
		if (s == null) return null;
		int startIdx = s.indexOf(from);
		if (startIdx < 0) return s;
		int endIdx = s.indexOf(to, startIdx + from.length());
		if (endIdx < 0) return s;
		return s.substring(0, startIdx) + s.substring(endIdx + to.length());
	}

	public static String cutFromTo(String s, String from, String to) {
		if (s == null) return null;
		s = cutFrom(s, from);
		s = cutTo(s, to);
		return s;
	}

	public static String cutFrom(String s, String from) {
		if (s == null) return null;
		int fromIdx = s.indexOf(from);
		if (fromIdx < 0) return null;
		fromIdx += from.length();
		return s.substring(fromIdx);
	}

	public static String cutTo(String s, String to) {
		if (s == null) return null;
		int toIdx = s.indexOf(to);
		if (toIdx < 0) return null;
		return s.substring(0, toIdx);
	}

	public static String toHtmlId(Object... objects) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Object object : objects) {
			if (first) {
				first = false;
			} else {
				sb.append("_");
			}
			if (object == null) {
				sb.append("null");
				continue;
			}
			sb.append(toHtmlId(object.toString()));
		}
		return sb.toString();
	}

	public static String toHtmlId(String s) {
		int len = s.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char ch = s.charAt(i);
			if (Character.isLetter(ch) || (Character.isDigit(ch) && i > 0)) {
				sb.append(ch);
				continue;
			}
			sb.append('_');
		}
		return sb.toString();
	}

	// TODO rename
	public static String cutLeft(String s, int maxlength) {
		if (s.length() > maxlength) {
			return s.substring(s.length() - maxlength);
		} else return s;
	}

	// TODO rename
	public static String fillUpRight(String s, String filler, int minLength) {
		StringBuilder sb = new StringBuilder(s);
		while (sb.length() < minLength) {
			sb.append(filler);
		}
		return sb.toString();
	}

	public static boolean isLink(String s) {
		if (isBlank(s)) return false;
		if (s.contains(" ")) return false;
		if (s.contains("\n")) return false;
		if (s.startsWith("http://")) return true;
		if (s.startsWith("https://")) return true;
		if (s.startsWith("www.")) return true;
		if (s.startsWith("ftp://")) return true;
		return false;
	}

	public static boolean isEmail(String s) {
		if (isBlank(s)) return false;
		if (s.length() < 5) return false;
		boolean at = false;
		boolean dot = false;
		int len = s.length();
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if (c == '@') {
				if (at) return false;
				at = true;
				continue;
			}
			if (c == '.') {
				dot = true;
				continue;
			}
			if (Character.isLetterOrDigit(c) || c == '-' || c == '_' || c == '+') continue;
			return false;
		}
		if (!dot || !at) return false;
		return true;
	}

	public static boolean isTrue(String s) {
		if (s == null) return false;
		s = s.toLowerCase();
		if (s.equals("true")) return true;
		if (s.equals("yes")) return true;
		if (s.equals("y")) return true;
		if (s.equals("1")) return true;
		if (s.equals("ja")) return true;
		if (s.equals("j")) return true;
		return false;
	}

	public static int indexOf(String text, String[] toFind, int startIdx) {
		int firstIdx = -1;
		for (int i = 0; i < toFind.length; i++) {
			int idx = text.indexOf(toFind[i], startIdx);
			if (firstIdx < 0 || (idx >= 0 && idx < firstIdx)) {
				firstIdx = idx;
			}
		}
		return firstIdx;
	}

	public static String format(Object o) {
		if (o == null) return null;
		if (o instanceof Object[]) return formatObjectArray((Object[]) o);
		if (o instanceof Map) return formatMap((Map) o);
		if (o instanceof Collection) formatCollection((Collection) o);
		if (o instanceof Enumeration) return formatEnumeration((Enumeration) o);
		if (o instanceof Throwable) return formatException((Throwable) o);
		return o.toString();
	}

	private static boolean isWrapperException(Throwable ex) {
		if (getSimpleName(ex.getClass()).equals("RuntimeException")) return true;
		if (getSimpleName(ex.getClass()).equals("ExecutionException")) return true;
		if (getSimpleName(ex.getClass()).equals("UmbrellaException")) return true;
		return false;
	}

	public static String formatEnumeration(Enumeration e) {
		return formatCollection(Utl.toList(e));
	}

	public static String formatCollection(Collection c) {
		return formatObjectArray(c.toArray());
	}

	public static String formatMap(Map map) {
		StringBuilder sb = new StringBuilder();
		sb.append("map[");
		sb.append(map.size());
		sb.append("]={");
		boolean following = false;
		Set<Map.Entry> entries = map.entrySet();
		for (Map.Entry entry : entries) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			if (following) {
				sb.append(',');
			}
			following = true;
			sb.append('"');
			sb.append(format(key));
			sb.append("\"=\"");
			sb.append(format(value));
			sb.append('"');
		}
		sb.append('}');
		return sb.toString();
	}

	public static String formatException(Throwable ex) {
		StringBuilder sb = null;
		while (ex != null) {
			Throwable cause = ex.getCause();
			String message = ex.getMessage();
			if (cause != null && message != null && message.startsWith(cause.getClass().getName())) message = null;
			while ((isWrapperException(ex) && isBlank(message) && cause != null)
					|| getSimpleName(ex.getClass()).equals("UmbrellaException")) {
				ex = cause;
				cause = ex.getCause();
				message = ex.getMessage();
				if (cause != null && message != null && message.startsWith(cause.getClass().getName())) message = null;
			}
			if (sb == null) {
				sb = new StringBuilder();
			} else {
				sb.append("\nCaused by ");
			}
			if (!isWrapperException(ex)) {
				sb.append(getSimpleName(ex.getClass()));
				sb.append(": ");
			}
			sb.append(message);
			ex = cause;
		}
		return sb.toString();
	}

	public static String formatStackTrace(StackTraceElement[] trace) {
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement element : trace)
			sb.append("    at ").append(element).append("\n");
		return sb.toString();
	}

	public static String getStackTrace(Throwable t) {
		StringBuilder sb = new StringBuilder();
		sb.append(t.toString()).append("\n");
		sb.append(formatStackTrace(t.getStackTrace()));

		Throwable cause = t.getCause();
		if (cause == null) return sb.toString();
		sb.append("Caused by: ").append(getStackTrace(cause));

		return sb.toString();
	}

	public static String formatObjectArray(Object[] oa) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (int i = 0; i < oa.length; i++) {
			if (oa[i] != null) {
				sb.append('<');
				sb.append(format(oa[i]));
				sb.append('>');
			}
			if (i != oa.length - 1) {
				sb.append(',');
			}
		}
		sb.append('}');
		return sb.toString();
	}

	public static String getSimpleName(Class type) {
		String name = type.getName();
		int idx = name.lastIndexOf('.');
		if (idx > 0) {
			name = name.substring(idx + 1);
		}
		return name;
	}

	public static boolean isBlank(String s) {
		return s == null || s.length() == 0 || s.trim().length() == 0;
	}
}
