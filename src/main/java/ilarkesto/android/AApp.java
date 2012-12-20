package ilarkesto.android;

import ilarkesto.android.view.ASkin;
import ilarkesto.core.logging.Log;
import android.app.Application;
import android.content.Context;

public abstract class AApp extends Application {

	private Log log = Log.get(getClass());
	protected static Context context;

	private AUserTracker userTracker;
	private FilesCache filesCache;
	private ASkin skin;

	static {
		Log.setLogRecordHandler(new AndroidLogDatahandler());
	}

	public abstract Class<? extends AActivity<? extends AApp>> getHomeActivity();

	public AApp() {
		initResources();
	}

	@Override
	public void onCreate() {
		context = this;
		super.onCreate();
	}

	protected void initResources() {}

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

	protected ASkin createSkin() {
		return new ASkin(context);
	}

	public ASkin getSkin() {
		if (skin == null) skin = createSkin();
		return skin;
	}

	public FilesCache getFilesCache() {
		if (filesCache == null) filesCache = new FilesCache(context, "default");
		return filesCache;
	}

	public static AApp get() {
		if (context == null) throw new IllegalStateException("context == null -> AApp.onCreate() not caled yet.");
		return (AApp) context.getApplicationContext();
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
