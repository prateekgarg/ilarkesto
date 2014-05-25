package ilarkesto.core.base;

public class RuntimeTracker {

	private final long starttime = System.currentTimeMillis();
	private long runtime = -1;

	public long getRuntime() {
		if (runtime >= 0) return runtime; // stopped
		return System.currentTimeMillis() - starttime; // still running
	}

	public void stop() {
		if (runtime >= 0) throw new IllegalStateException("Already stopped");
		runtime = System.currentTimeMillis() - starttime;
	}

	public String getRuntimeFormated() {
		long runtime = getRuntime();
		if (runtime < 10000) return runtime + " msec";
		runtime = runtime / 1000;
		if (runtime < 180) return runtime + " sec";
		runtime = runtime / 60;
		return runtime + " min";
	}

	@Override
	public String toString() {
		return getRuntimeFormated();
	}
}
