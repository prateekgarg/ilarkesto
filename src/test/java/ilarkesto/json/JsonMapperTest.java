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

import ilarkesto.testng.ATest;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

public class JsonMapperTest extends ATest {

	@Test
	public void serialize() {
		assertEquals(JsonMapper.serialize(null), "null");
		assertEquals(JsonMapper.serialize("witek"), "\"witek\"");
		assertEquals(JsonMapper.serialize(23), "23");
		assertEquals(JsonMapper.serialize(true), "true");
		assertEquals(JsonMapper.serialize(1.1), "1.1");

		assertEquals(JsonMapper.serialize(new Dummy()), "{\"a\":23,\"b\":[1,2]}");
	}

	public static class Dummy {

		private int a = 23;
		private List<Integer> b = Arrays.asList(1, 2);

	}

}
