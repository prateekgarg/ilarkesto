package ilarkesto.android;

import ilarkesto.android.view.ASkin;
import ilarkesto.core.logging.Log;

import java.lang.reflect.Field;

import android.app.Application;
import android.content.Context;

public abstract class AApp extends Application {

	private Log log = Log.get(getClass());
	protected static Context context;

	private AUserTracker userTracker;
	private FilesCache filesCache;
	private ASkin skin;
	private Localizer localizer;

	static {
		Log.setLogRecordHandler(new AndroidLogDatahandler());
	}

	public abstract Class<? extends AActivity<? extends AApp>> getHomeActivity();

	public AApp() {
		copyResourceIds();
	}

	@Override
	public void onCreate() {
		context = this;
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		if (userTracker != null) userTracker.shutdown();
		super.onTerminate();
	}

	@Deprecated
	protected void initResources() {}

	private void copyResourceIds() {
		String sourceClassName = getClass().getPackage().getName() + ".R";
		Class source;
		try {
			source = Class.forName(sourceClassName);
		} catch (ClassNotFoundException e) {
			log.warn("Missing resource class", sourceClassName);
			return;
		}

		copyResourceIds(source, R.class, "layout");
		copyResourceIds(source, R.class, "color");
		copyResourceIds(source, R.class, "id");
		copyResourceIds(source, R.class, "drawable");
		copyResourceIds(source, R.class, "string");
	}

	private void copyResourceIds(Class source, Class destination, String subclassname) {
		copyResourceIds(getSubclass(source, subclassname), getSubclass(destination, subclassname));
	}

	private Class getSubclass(Class c, String subclassname) {
		for (Class subclass : c.getDeclaredClasses()) {
			if (subclass.getSimpleName().equals(subclassname)) return subclass;
		}
		log.error("Missing subclass", subclassname, "in", c);
		return null;
	}

	private void copyResourceIds(Class source, Class destination) {
		for (Field dstField : destination.getFields()) {
			Field srcField;
			try {
				srcField = source.getField(dstField.getName());
			} catch (Exception ex) {
				log.warn("Reading field", dstField.getName(), "from", source, "failed.", ex);
				continue;
			}
			try {
				dstField.set(null, srcField.get(null));
			} catch (Throwable ex) {
				log.error("Writing field", dstField.getName(), "to", destination, "failed.", ex);
			}
		}
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

	public Localizer getLocalizer() {
		if (localizer == null) localizer = new Localizer(context);
		return localizer;
	}

	public static AApp get() {
		if (context == null) throw new IllegalStateException("context == null -> AApp.onCreate() not caled yet.");
		return (AApp) context.getApplicationContext();
	}

	class DummyUserTracker extends AUserTracker {

		@Override
		protected void onTrack(String path) {}

		@Override
		protected void onFlush() {}

		@Override
		protected void onShutdown() {}

		@Override
		public String toString() {
			return "dummy";
		}

	}

}
