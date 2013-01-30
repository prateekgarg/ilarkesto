package ilarkesto.json;

import ilarkesto.core.base.RuntimeTracker;
import ilarkesto.core.logging.Log;

import java.io.File;

public abstract class ARemoteJsonCache<P extends AJsonWrapper> extends AJsonWrapper {

	private Log log = Log.get(getClass());

	protected abstract Class<P> getPayloadType();

	protected abstract P onUpdate(P payload, boolean forced);

	public ARemoteJsonCache(File file) {
		super(new JsonObject(file));
	}

	public P getPayload() {
		return createFromObject("payload", getPayloadType());
	}

	public P getPayload_ButUpdateIfNull() {
		P payload = getPayload();
		if (payload == null || isInvalidated()) {
			update(true);
			return getPayload();
		}
		return payload;
	}

	public synchronized void update(boolean force) {
		P payload = getPayload();
		log.info("Updating payload", force ? "(forced)" : "");
		RuntimeTracker rt = new RuntimeTracker();
		try {
			payload = onUpdate(payload, force);
		} catch (Throwable ex) {
			log.error("Updating payload failed.", ex);
			payload = null;
		}
		log.info("Payload updated in", rt.getRuntimeFormated());
		if (payload == null) return;
		json.put("payload", payload);
		json.put("lastUpdated", System.currentTimeMillis());
		save();
	}

	public synchronized void save() {
		log.info("Saving");
		json.save();
	}

	public synchronized void invalidatePayload() {
		json.put("invalidated", true);
		save();
	}

	public boolean isInvalidated() {
		return json.isTrue("invalidated");
	}

	public Long getLastUpdated() {
		return json.getLong("lastUpdated");
	}

	public long getTimeSinceLastUpdated() {
		Long lastUpdated = getLastUpdated();
		if (lastUpdated == null) return Long.MAX_VALUE;
		return System.currentTimeMillis() - lastUpdated;
	}

	public long getDaysSinceLastUpdated() {
		return getTimeSinceLastUpdated() / 86400000l;
	}

}
