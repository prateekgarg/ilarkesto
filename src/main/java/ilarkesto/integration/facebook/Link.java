package ilarkesto.integration.facebook;

import ilarkesto.core.base.Str;
import ilarkesto.core.json.JsonObject;

public class Link extends FeedItem {

	public Link(JsonObject data) {
		super(data);
	}

	@Override
	public String getBestText() {
		StringBuilder sb = new StringBuilder();
		String description = getDescription();
		if (!Str.isBlank(description)) {
			sb.append(description);
		} else {
			sb.append(getName());
		}
		sb.append(" -> ");
		sb.append(getBestLink());
		return sb.toString();
	}

	@Override
	public String getBestImage() {
		String picture = getPicture();
		if (!Str.isBlank(picture)) return picture;
		return getIcon();
	}

	@Override
	public String getBestLink() {
		return getLink();
	}

	public String getDescription() {
		return data.getString("description");
	}

	public String getLink() {
		return data.getString("link");
	}

	public String getPicture() {
		return data.getString("picture");
	}

	public String getIcon() {
		return data.getString("icon");
	}

	public String getName() {
		return data.getString("name");
	}

	public String getObjectId() {
		return data.getString("object_id");
	}

}
