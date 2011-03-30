package ilarkesto.integration.facebook;

import ilarkesto.core.json.JsonObject;

import java.util.Date;

public class FeedItem extends Item {

	public FeedItem(JsonObject data) {
		super(data);
	}

	public String getBestImage() {
		return null;
	}

	public String getBestText() {
		return null;
	}

	public String getBestLink() {
		return null;
	}

	public Person getFrom() {
		if (!data.contains("from")) return null;
		return new Person(data.getObject("from"));
	}

	public Date getCreatedTime() {
		return data.getDate("created_time", DATE_FORMAT);
	}

	public Date getUpdatedTime() {
		return data.getDate("updated_time", DATE_FORMAT);
	}

	public String getType() {
		return data.getString("status");
	}

}
