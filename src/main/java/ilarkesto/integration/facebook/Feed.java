package ilarkesto.integration.facebook;

import ilarkesto.core.json.JsonObject;
import ilarkesto.core.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class Feed extends Item {

	private static final Log log = Log.get(Feed.class);

	public Feed(JsonObject data) {
		super(data);
	}

	public List<FeedItem> getData() {
		List<JsonObject> items = data.getArrayOfObjects("data");
		List<FeedItem> ret = new ArrayList<FeedItem>(items.size());
		for (JsonObject item : items) {
			ret.add(createItem(item));
		}
		return ret;
	}

	private FeedItem createItem(JsonObject json) {
		String type = json.getString("type");
		if ("status".equals(type)) return new Status(json);
		if ("link".equals(type)) return new Link(json);
		if ("photo".equals(type)) return new Photo(json);
		if ("video".equals(type)) return new Video(json);
		if ("swf".equals(type)) return new Video(json);
		if (type != null) log.warn("Unsupported feed item:", type, json);
		return new FeedItem(json);
	}

}
