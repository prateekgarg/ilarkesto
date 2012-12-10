package ilarkesto.core.html;

import java.util.Collection;

public class Html {

	public static String concatToHtml(Collection<? extends ToHtmlSupport> items, String separator) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (ToHtmlSupport entity : items) {
			if (first) {
				first = false;
			} else {
				sb.append(separator);
			}
			sb.append(entity.toHtml());
		}
		return sb.toString();
	}

}
