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

import ilarkesto.testng.ATest;

import java.util.List;

public class HochschulkompassTest extends ATest {

	public void values() {
		ValuesCache valuesCache = new ValuesCache(getTestOutputFile("values.json"));
		valuesCache.delete();
		valuesCache.update(true);
		Values values = valuesCache.getPayload();

		assertValueList("subjects", values.getSubjects());
		assertValueList("subjectgroups", values.getSubjectgroups());
	}

	private void assertValueList(String name, List<? extends Value> list) {
		log.info(name, list.size(), "->", list);
		assertNotEmpty(list);
	}

}
