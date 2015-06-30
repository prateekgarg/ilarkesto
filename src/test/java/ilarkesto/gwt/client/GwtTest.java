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
package ilarkesto.gwt.client;

import ilarkesto.testng.ATest;

import java.util.LinkedHashMap;
import java.util.Map;

import org.testng.annotations.Test;

public class GwtTest extends ATest {

	@Test
	public void parseHistoryToken() {
		assertToken("");
		assertToken("Activity", "Activity=Activity");
		assertToken("Activity/", "Activity=Activity");
		assertToken("Activity/param1=a/param2=b", "Activity=Activity", "param1=a", "param2=b");
		assertToken("Activity/param1=a/param2=b/", "Activity=Activity", "param1=a", "param2=b");
	}

	private void assertToken(String token, String... expectedValues) {
		LinkedHashMap<String, String> result = Gwt.parseHistoryToken(token);
		int i = 0;
		for (Map.Entry<String, String> entry : result.entrySet()) {
			assertEquals(entry.getKey() + "=" + entry.getValue(), expectedValues[i]);
			i++;
		}
	}
}
