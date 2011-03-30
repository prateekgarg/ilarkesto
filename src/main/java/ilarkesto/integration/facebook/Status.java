package ilarkesto.integration.facebook;

import ilarkesto.core.json.JsonObject;

public class Status extends FeedItem {

	public Status(JsonObject data) {
		super(data);
	}

	@Override
	public String getBestText() {
		return getMessage();
	}

	@Override
	public String getBestImage() {
		return null;
	}

	@Override
	public String getBestLink() {
		return null;
	}

	public String getMessage() {
		return data.getString("message");
	}

}
