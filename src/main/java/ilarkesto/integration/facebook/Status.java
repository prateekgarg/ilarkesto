package ilarkesto.integration.facebook;

import ilarkesto.json.JsonObject;

public class Status extends FeedItem {

	public Status(JsonObject data) {
		super(data);
	}

	/**
	 * The status message content
	 */
	public final String getMessage() {
		return data.getString("message");
	}
}
