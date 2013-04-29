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
package ilarkesto.integration.hochschulkompass;

import ilarkesto.core.base.Parser;
import ilarkesto.core.base.Parser.ParseException;
import ilarkesto.core.base.Str;
import ilarkesto.io.IO;
import ilarkesto.json.ARemoteJsonCache;
import ilarkesto.json.JsonObject;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class ValuesCache extends ARemoteJsonCache<Values> {

	public ValuesCache(File file) {
		super(Values.class, file);
	}

	@Override
	protected Values onUpdate(Values payload, boolean forced) {
		String url = HochschulkompassSearchUrl.getBaseUrl();
		String data = IO.downloadUrlToString(url);
		if (payload == null) payload = new Values(new JsonObject());
		try {
			updateAll(payload, data);
		} catch (ParseException ex) {
			throw new RuntimeException("Parsing failed page failed: " + url + "->" + data, ex);
		}
		return payload;
	}

	private void updateAll(Values values, String data) throws ParseException {
		values.setSubjects(parseSubjects(data));
		values.setSubjectgroups(parseSubjectgroups(data));
	}

	private List<Subjectgroup> parseSubjectgroups(String data) throws ParseException {
		List<Subjectgroup> ret = new LinkedList<Subjectgroup>();
		String s = Str.cutFromTo(data, "id=\"sachgr\"", "</select>");
		Parser parser = new Parser(s);
		while (parser.gotoAfterIf("<option ")) {
			parser.gotoAfter("class=\"");
			boolean top = parser.isNext("opt_1");
			parser.gotoAfter("value=\"");
			String key = parser.getUntil("\"");
			parser.gotoAfter(">");
			String value = parser.getUntil("</option>");
			ret.add(new Subjectgroup(key, parseHtml(value), top));
		}
		return ret;
	}

	private List<Subject> parseSubjects(String data) throws ParseException {
		List<Subject> ret = new LinkedList<Subject>();
		String s = Str.cutFromTo(data, "id=\"sacha1\"", "</select>");
		Parser parser = new Parser(s);
		parser.gotoAfter("</option>"); // skip first option ('no selection')
		while (parser.gotoAfterIf("<option ")) {
			parser.gotoAfter("value=\"");
			String key = parser.getUntil("\"");
			parser.gotoAfter(">");
			String value = parser.getUntil("</option>");
			ret.add(new Subject(key, parseHtml(value)));
		}
		return ret;
	}

	private String parseHtml(String s) {
		if (s == null) return null;
		s = s.replace("&nbsp;", " ");
		s = s.trim();
		return s;
	}

}
