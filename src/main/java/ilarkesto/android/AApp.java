package ilarkesto.android;

import ilarkesto.core.logging.Log;
import android.app.Application;
import android.content.Context;

public abstract class AApp extends Application {

	private Log log = Log.get(getClass());
	protected Context context = this;

	private AUserTracker userTracker;

	static {
		Log.setLogRecordHandler(new AndroidLogDatahandler());
	}

	public AUserTracker getUserTracker() {
		if (userTracker == null) {
			userTracker = createUserTracker();
			log.info("User tracker created:", userTracker);
		}
		return userTracker;
	}

	protected AUserTracker createUserTracker() {
		return new DummyUserTracker();
	}

	class DummyUserTracker extends AUserTracker {

		@Override
		protected void onTrack(String path) {
			// nop
		}

		@Override
		public String toString() {
			return "dummy";
		}

	}

}
