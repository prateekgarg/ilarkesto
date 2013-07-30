package ilarkesto.core.base;

public interface OperationObserver {

	public static final String DOWNLOADING = "downloading";
	public static final String LOADING_CACHE = "loadingCache";
	public static final String UPDATING = "updating";

	boolean isAbortRequested();

	void onOperationInfoChanged(String key, Object... arguments);

	public static final OperationObserver DUMMY = new OperationObserver() {

		@Override
		public boolean isAbortRequested() {
			return false;
		}

		@Override
		public void onOperationInfoChanged(String key, Object... arguments) {}
	};

}
