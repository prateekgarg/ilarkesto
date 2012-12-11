package ilarkesto.core.base;

public class RuntimeTracker {

	private final long starttime = System.currentTimeMillis();

	public long getRuntime() {
		return System.currentTimeMillis() - starttime;
	}

	public String getRuntimeFormated() {
		long runtime = getRuntime();
		if (runtime < 2000) return runtime + " msec";
		runtime = runtime / 1000;
		if (runtime < 120) return runtime + " sec";
		runtime = runtime / 60;
		return runtime + " min";
	}

	@Override
	public String toString() {
		return getRuntimeFormated();
	}
}
