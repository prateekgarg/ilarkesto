package ilarkesto.json;

import ilarkesto.core.base.RuntimeTracker;
import ilarkesto.core.logging.Log;

import java.io.File;

public abstract class ARemoteJsonCache<P extends AJsonWrapper> {

	protected Log log = Log.get(getClass());

	protected abstract P onUpdate(P payload, boolean forced);

	private Class<P> payloadType;
	private File file;

	private JsonObject wrapper;

	public ARemoteJsonCache(Class<P> payloadType, File file) {
		this.payloadType = payloadType;
		this.file = file;
	}

	private synchronized JsonObject getJson() {
		if (wrapper == null) wrapper = JsonObject.loadFile(file, true);
		return wrapper;
	}

	public P getPayload() {
		JsonObject json = getJson();
		P payload = AJsonWrapper.createWrapper(json.getObject("payload"), payloadType);
		if (payload == null) {
			payload = createInitialPayload();
			if (payload != null) json.put("payload", payload.json);
		}
		return payload;
	}

	protected P createInitialPayload() {
		return null;
	}

	public P getPayload_ButUpdateIfNull() throws RemoteUpdateFailedException {
		P payload = getPayload();
		if (payload == null) {
			log.info("Payload does not exist, needs update");
			update(true);
			return getPayload();
		}
		if (isInvalidated()) {
			log.info("Payload is invalidated, needs update");
			update(true);
			return getPayload();
		}
		return payload;
	}

	public synchronized void update(boolean force) throws RemoteUpdateFailedException {
		P payload = getPayload();
		log.info("Updating payload", force ? "(forced)" : "");
		RuntimeTracker rt = new RuntimeTracker();
		try {
			payload = onUpdate(payload, force);
		} catch (Throwable ex) {
			if (payload != null) {
				log.info("Updating payload failed.", ex);
				return;
			}
			throw new RemoteUpdateFailedException("Updating payload failed.", ex);
		}
		if (payload == null) {
			// payload not updated (perhaps not due)
			log.info("Payload not updated after", rt.getRuntimeFormated());
			if (!force) return;
			throw new RemoteUpdateFailedException("Loading payload failed.");
		}
		log.info("Payload updated in", rt.getRuntimeFormated());
		JsonObject json = getJson();
		json.put("payload", payload);
		json.put("lastUpdated", System.currentTimeMillis());
		json.remove("invalidated");
		save();
	}

	public synchronized void save() {
		log.info("Saving");
		if (wrapper != null) wrapper.write(file, false);
	}

	public synchronized void delete() {
		log.info("Deleting");
		wrapper = null;
		file.delete();
	}

	public synchronized void unload() {
		wrapper = null;
	}

	public synchronized void invalidatePayload() {
		getJson().put("invalidated", true);
		save();
	}

	public File getFile() {
		return file;
	}

	public boolean isInvalidated() {
		return getJson().isTrue("invalidated");
	}

	public Long getLastUpdated() {
		return getJson().getLong("lastUpdated");
	}

	public long getTimeSinceLastUpdated() {
		Long lastUpdated = getLastUpdated();
		if (lastUpdated == null) return Long.MAX_VALUE;
		return System.currentTimeMillis() - lastUpdated;
	}

	public long getHoursSinceLastUpdated() {
		return getTimeSinceLastUpdated() / 3600000l;
	}

	public long getDaysSinceLastUpdated() {
		return getTimeSinceLastUpdated() / 86400000l;
	}

}
