package ilarkesto.core.base;

public interface OperationObserver {

	boolean isAbortRequested();

	void onInfoChanged(String key, Object... arguments);

}
