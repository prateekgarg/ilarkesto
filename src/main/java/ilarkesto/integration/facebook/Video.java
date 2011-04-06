package ilarkesto.integration.facebook;

import ilarkesto.core.base.Str;
import ilarkesto.core.json.JsonObject;

public class Video extends FeedItem {

	public Video(JsonObject data) {
		super(data);
	}

	@Override
	public String getBestText() {
		StringBuilder sb = new StringBuilder();

		String description = getDescription();
		if (!Str.isBlank(description)) {
			sb.append(description).append("\n\n");
		}

		sb.append(getLink());

		return sb.toString();
	}

	public String getSource() {
		return data.getString("source");
	}

}
