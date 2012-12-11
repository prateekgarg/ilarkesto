package ilarkesto.android;

import ilarkesto.core.logging.Log;
import android.content.Context;

public abstract class AUserTracker {

	private Log log = Log.get(getClass());

	protected abstract void onTrack(String path);

	public final void track(String location) {
		log.info(location);
		onTrack(location);
	}

	public final void track(Object... location) {
		track((String) null, location);
	}

	public final void track(Context context, Object... location) {
		track(context.getClass().getSimpleName(), location);
	}

	public final void track(String prefix, Object... location) {
		StringBuilder sb = new StringBuilder();
		if (prefix != null) sb.append(prependTrackingSeparator(prefix));
		for (Object s : location) {
			sb.append(prependTrackingSeparator(s));
		}
		track(sb.toString());
	}

	private String prependTrackingSeparator(Object o) {
		String s = o == null ? "/" : o.toString();
		if (!s.startsWith("/")) s = "/" + s;
		return s;
	}
}
