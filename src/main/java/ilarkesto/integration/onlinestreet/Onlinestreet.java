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
package ilarkesto.integration.onlinestreet;

import ilarkesto.base.Sys;
import ilarkesto.base.Utl;
import ilarkesto.core.base.Str;
import ilarkesto.io.IO;

import java.util.ArrayList;
import java.util.List;

public class Onlinestreet {

	public static void main(String[] args) {
		Sys.setHttpProxy("83.246.65.146", 80);
		System.out.println(Str.format(getCitiesByPlz(30159)));
		System.out.println(Str.format(getCitiesByPlz(31737)));
		System.exit(0);
	}

	public static String PLZ_URL_TEMPLATE = "http://onlinestreet.de/plz/{*}.html";

	public static String getCityByPlz(Integer plz) {
		List<String> cities = getCitiesByPlz(plz);
		if (cities.isEmpty()) return null;
		return cities.get(0);
	}

	public static List<String> getCitiesByPlz(Integer plz) {
		if (plz == null) return null;
		String url = PLZ_URL_TEMPLATE.replace("{*}", plz.toString());
		String html = IO.downloadUrlToString(url, "windows-1252");

		// Einzeltreffer
		String city = Str.cutFromTo(html, "ist als Postleitzahl dem Ort ", " (");
		if (city != null) return Utl.toList(city);

		// Mehrfachtreffer
		List<String> cities = new ArrayList<String>();
		int idx = html.indexOf("<tr><td>");
		while (idx >= 0) {
			idx += 8;
			int startIdx = html.indexOf(">", idx);
			if (startIdx < 0) continue;
			int endIdx = html.indexOf(" <", startIdx);
			if (endIdx < 0) continue;
			city = html.substring(startIdx + 1, endIdx);
			if (city != null) cities.add(city.trim());
			idx = html.indexOf("<tr><td>", idx);
		}
		return cities;
	}

}
