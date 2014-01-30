package ilarkesto.core.base;

import ilarkesto.core.logging.Log;

public interface OperationObserver {

	public static final String DOWNLOADING = "downloading";
	public static final String LOADING_CACHE = "loadingCache";
	public static final String UPDATING = "updating";
	public static final String SAVING = "saving";

	boolean isAbortRequested();

	void onOperationInfoChanged(String key, Object... arguments);

	public static final OperationObserver DUMMY = new OperationObserver() {

		private final Log log = Log.get(OperationObserver.class);

		@Override
		public boolean isAbortRequested() {
			return false;
		}

		@Override
		public void onOperationInfoChanged(String key, Object... arguments) {
			log.info(key, "->", Str.formatObjectArray(arguments));
		}
	};

}
