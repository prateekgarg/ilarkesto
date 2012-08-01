package ilarkesto.integration.facebook;

import ilarkesto.core.logging.Log;
import ilarkesto.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class MeFeed extends AObject {

	private static Log log = Log.get(MeFeed.class);

	public MeFeed(JsonObject json) {
		super(json);
		if (!json.contains("data")) throw new IllegalStateException("Missing data in MeFeed: " + json.toString());
	}

	public List<AIdentity> getData() {
		List<JsonObject> items = json.getArrayOfObjects("data");
		List<AIdentity> ret = new ArrayList<AIdentity>(items.size());
		for (JsonObject jItem : items) {
			AIdentity item = createObject(jItem);
			if (item == null) continue;
			ret.add(item);
		}
		return ret;
	}

	private AIdentity createObject(JsonObject json) {
		String type = json.getString("type");
		if (type == null) {
			log.error("Object has no type:", json);
			return null;
		}
		if ("status".equals(type)) return new Status(json);
		if ("link".equals(type)) return new Link(json);
		if ("photo".equals(type)) return new Photo(json);
		if ("video".equals(type)) return new Video(json);
		if ("swf".equals(type)) return new Video(json);

		log.error("Unknown object type:", type, "->", json);
		return null;
	}

}
