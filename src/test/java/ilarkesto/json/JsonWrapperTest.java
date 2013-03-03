package ilarkesto.json;

import ilarkesto.testng.ATest;

import java.util.List;

import org.testng.annotations.Test;

public class JsonWrapperTest extends ATest {

	@Test
	public void array() {
		Dummy dummy = new Dummy("parent");
		Dummy child1 = new Dummy("child 1");
		Dummy child2 = new Dummy("child 2");

		dummy.addSubdummy(child1);
		assertContains(dummy.getSubdummys(), child1);

		List<Dummy> subdummys = dummy.getSubdummys();
		subdummys.add(child2);
		assertContains(dummy.getSubdummys(), child2);
	}

	@Test
	public void equals() {
		Dummy a = new Dummy(JsonObject.parse("{\"aBool\":true}"));
		Dummy b = new Dummy(JsonObject.parse("{ \"aBool\": true }"));
		assertEquals(a, b);

		Dummy c = new Dummy(JsonObject.parse("{\"aBool\":false}"));
		assertNotEquals(a, c);
	}

	static class Dummy extends AJsonWrapper {

		public Dummy(JsonObject json) {
			super(json);
		}

		public Dummy(String name) {
			json.put("name", name);
		}

		public List<Dummy> getSubdummys() {
			return getWrapperArray("subdummys", Dummy.class);
		}

		public void addSubdummy(Dummy sub) {
			json.addToArray("subdummys", sub);
		}

	}

}
