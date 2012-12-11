package ilarkesto.android;

import ilarkesto.core.base.Str;
import ilarkesto.core.logging.Log.Level;
import ilarkesto.core.logging.LogRecord;
import ilarkesto.core.logging.LogRecordHandler;
import android.util.Log;

public class AndroidLogDatahandler implements LogRecordHandler {

	private boolean onAndroid = true;

	public AndroidLogDatahandler() {
		try {
			android.util.Log.isLoggable("test", Log.INFO);
		} catch (UnsatisfiedLinkError ex) {
			onAndroid = false;
		}
	}

	@Override
	public void log(LogRecord record) {
		if (!onAndroid) {
			System.out.println(record.level + " " + record.name + " " + record.getText());
			return;
		}
		int androidLevel = toAndroidLevel(record.level);
		String androidTag = getLogTag(record);
		if (!android.util.Log.isLoggable(androidTag, androidLevel)) return;
		switch (androidLevel) {
			case Log.ERROR:
				Log.e(androidTag, record.getText());
				return;
			case Log.WARN:
				Log.w(androidTag, record.getText());
				return;
			case Log.INFO:
				Log.i(androidTag, record.getText());
				return;
			case Log.DEBUG:
				Log.d(androidTag, record.getText());
				return;
		}
		Log.v(androidTag, record.getText());
	}

	private String getLogTag(LogRecord record) {
		return Str.cutRight(record.name, 23);
	}

	private int toAndroidLevel(Level level) {
		switch (level) {
			case FATAL:
			case ERROR:
				return Log.ERROR;
			case WARN:
				return Log.WARN;
			case INFO:
				// case DEBUG:
				return Log.INFO;
		}
		return Log.DEBUG;
	}

	@Override
	public void flush() {}
}
