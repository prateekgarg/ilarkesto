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

import ilarkesto.json.JsonMapper.TypeResolver;
import ilarkesto.json.JsonSaxParser.ParseException;
import ilarkesto.testng.ATest;

import java.io.IOException;
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

		assertEquals(JsonMapper.serialize(new Dummy(23, Arrays.asList(1l, 2l), new SubDummy())),
			"{\n \"a\": 23,\n \"b\": [ 1, 2 ],\n \"c\": {\n  \"at\": \"@\"\n },\n \"d\": null\n}");
	}

	@Test
	public void deserialize() throws IOException, ParseException {
		Dummy dummy = JsonMapper.deserialize(
			"{\n \"a\": 23,\n \"b\": [ 1, 2 ],\n \"c\": {\n  \"at\": \"@\"\n },\n \"d\": [ {\"at\": \"@\"} ]\n}",
			Dummy.class, TYPE_RESOLVER);
		assertNotNull(dummy);

		assertEquals(dummy.a, 23);

		assertNotNull(dummy.b);
		assertNotEmpty(dummy.b);
		assertSize(dummy.b, 2);
		assertEquals(dummy.b.get(0).intValue(), 1);
		assertEquals(dummy.b.get(1).intValue(), 2);

		assertNotNull(dummy.c);
		assertEquals(dummy.c.at, "@");

		assertNotNull(dummy.d);
		assertSize(dummy.d, 1);
		assertEquals(dummy.d.get(0).at, "@");
	}

	public static class Dummy {

		private int a;
		private List<Long> b;
		private SubDummy c;
		private List<SubDummy> d;
		public transient String transy = "transy";

		public Dummy(int a, List<Long> b, SubDummy c) {
			super();
			this.a = a;
			this.b = b;
			this.c = c;
		}

		public Dummy() {}

	}

	public static class SubDummy {

		String at = "@";

	}

	private static final TypeResolver TYPE_RESOLVER = new TypeResolver() {

		@Override
		public Class resolveArrayType(Object object, String field) {
			if (object instanceof Dummy) {
				if (field.equals("d")) return SubDummy.class;
			}
			return null;
		}
	};

}
