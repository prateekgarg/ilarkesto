package ilarkesto.integration.velocity;

import ilarkesto.core.logging.Log;
import ilarkesto.core.logging.Log.Level;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

public class VelocityLogging implements LogChute {

	private static Log log = Log.get(VelocityLogging.class);

	@Override
	public void init(RuntimeServices rs) throws Exception {
		log.info("init", rs);
	}

	@Override
	public void log(int level, String message) {
		log.log(mapLevel(level), message);
	}

	@Override
	public void log(int level, String message, Throwable t) {
		log.log(mapLevel(level), message, t);
	}

	@Override
	public boolean isLevelEnabled(int level) {
		if (level > DEBUG_ID) return true;
		return log.isDebugEnabled();
	}

	private Level mapLevel(int level) {
		switch (level) {
			case ERROR_ID:
				return Level.ERROR;
			case WARN_ID:
				return Level.WARN;
			case INFO_ID:
				return Level.INFO;
			default:
				return Level.DEBUG;
		}
	}

}
