package ilarkesto.integration.facebook;

import ilarkesto.core.base.Str;
import ilarkesto.json.JsonObject;

public class Photo extends FeedItem {

	public Photo(JsonObject data) {
		super(data);
	}

	@Override
	public String getBestLink() {
		String link = getLink();
		if (!Str.isBlank(link)) return link;
		return getBestImage();
	}

}
