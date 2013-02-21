package ilarkesto.android;

import ilarkesto.core.base.Str;
import ilarkesto.core.logging.Log;
import android.content.Context;

public abstract class AUserTracker {

	private Log log = Log.get(getClass());

	protected abstract void onTrack(String path);

	protected abstract void onFlush();

	protected abstract void onShutdown();

	public final void track(String location) {
		log.info(location);
		onTrack(location);
	}

	public final void trackView(String viewName) {
		track("view: " + viewName);
	}

	public final void trackViewListItem(String listName, Object item) {
		StringBuilder sb = new StringBuilder();
		sb.append("view: ");
		sb.append(listName);
		if (item != null) {
			sb.append(" > ");
			sb.append(item);
		}
		track(sb.toString());
	}

	public final void trackAction(String action) {
		track("action: " + action);
	}

	public final void trackError(String message, Throwable ex) {
		StringBuilder sb = new StringBuilder();
		sb.append("error: ");
		if (message != null) sb.append(message);
		if (ex != null) sb.append(" > ").append(Str.formatException(ex));
		track(sb.toString());
	}

	@Deprecated
	public final void track(Object... location) {
		track((String) null, location);
	}

	@Deprecated
	public final void track(Context context, Object... location) {
		track(context.getClass().getSimpleName(), location);
	}

	@Deprecated
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

	public final void flush() {
		onFlush();
	}

	public final void shutdown() {
		flush();
		onShutdown();
	}

}
