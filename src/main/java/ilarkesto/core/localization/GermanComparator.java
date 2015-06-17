package ilarkesto.core.localization;

import java.util.Comparator;
import java.util.Locale;

/**
 * nach DIN 5007 Variante 1
 */
public class GermanComparator implements Comparator<String> {

	public static final GermanComparator INSTANCE = new GermanComparator();

	private GermanComparator() {}

	private String clean(String in) {
		return in.toLowerCase(Locale.GERMAN).replace('ö', 'o').replace('ä', 'a').replace('ü', 'u').replace('ß', 's');
	}

	@Override
	public int compare(String a, String b) {
		if (a == null && b == null) return 0;
		if (a == null && b != null) return -1;
		if (a != null && b == null) return 1;
		return clean(a).compareTo(clean(b));
	};

}
