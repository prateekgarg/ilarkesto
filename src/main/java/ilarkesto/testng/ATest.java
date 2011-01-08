package ilarkesto.testng;

import java.util.Collection;

import org.testng.Assert;

public class ATest extends Assert {

	public static final String OUTPUT_DIR = "test-output";

	public static <T> void assertContains(String string, String substring) {
		assertTrue(string.contains(substring), "<" + string + "> expected to contain <" + substring + ">");
	}

	public static <T> void assertContains(Collection<T> collection, T element) {
		assertTrue(collection.contains(element), "Collection expected to contain <" + element + ">");
	}

	public static void assertNotEmpty(Collection collection) {
		assertFalse(collection.isEmpty(), "Collection expected to be not empty, but it is");
	}

	public static void assertStartsWith(String actual, String expectedPrefix) {
		assertTrue(actual.startsWith(expectedPrefix), "<" + actual + "> expected to start with <" + expectedPrefix
				+ "> |");
	}

}
