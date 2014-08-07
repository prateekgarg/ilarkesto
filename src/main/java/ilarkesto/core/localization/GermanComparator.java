package ilarkesto.core.localization;

import java.util.Comparator;

/**
 * nach DIN 5007 Variante 1
 */
public class GermanComparator implements Comparator<String> {

	public static final GermanComparator INSTANCE = new GermanComparator();

	private GermanComparator() {}

	private String clean(String in) {
		return in.toLowerCase().replaceAll("ö", "o").replaceAll("ä", "a").replaceAll("ü", "u").replaceAll("ß", "ss");
	}

	@Override
	public int compare(String a, String b) {
		if (a == null && b == null) return 0;
		if (a == null && b != null) return -1;
		if (a != null && b == null) return 1;
		return clean(a).compareTo(clean(b));
	};

}
