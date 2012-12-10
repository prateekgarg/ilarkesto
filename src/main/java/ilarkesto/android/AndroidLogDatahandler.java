package ilarkesto.android;

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
		if (!android.util.Log.isLoggable(record.name, androidLevel)) return;
		switch (androidLevel) {
			case Log.ERROR:
				Log.e(record.name, record.getText());
				return;
			case Log.WARN:
				Log.w(record.name, record.getText());
				return;
			case Log.INFO:
				Log.i(record.name, record.getText());
				return;
			case Log.DEBUG:
				Log.d(record.name, record.getText());
				return;
		}
		Log.v(record.name, record.getText());
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
