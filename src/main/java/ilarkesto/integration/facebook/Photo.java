package ilarkesto.integration.facebook;

import ilarkesto.core.base.Str;
import ilarkesto.core.json.JsonObject;

public class Photo extends FeedItem {

	public Photo(JsonObject data) {
		super(data);
	}

	@Override
	public String getBestText() {
		return getName();
	}

	@Override
	public String getBestImage() {
		String picture = getPicture();
		if (!Str.isBlank(picture)) return picture;
		return getIcon();
	}

	@Override
	public String getBestLink() {
		return getBestImage();
	}

	public String getPicture() {
		return data.getString("picture");
	}

	public String getLink() {
		return data.getString("link");
	}

	public String getName() {
		return data.getString("name");
	}

	public String getIcon() {
		return data.getString("icon");
	}

	public String getObjectId() {
		return data.getString("object_id");
	}

}
