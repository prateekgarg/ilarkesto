package ilarkesto.integration.facebook;

import ilarkesto.json.JsonObject;

public class Photo extends FeedItem {

	public Photo(JsonObject data) {
		super(data);
	}

	/**
	 * The user provided caption given to this photo
	 */
	public final String getName() {
		return data.getString("name");
	}

	/**
	 * A link to the photo on Facebook
	 */
	public final String getLink() {
		return data.getString("link");
	}

	/**
	 * The thumbnail-sized source of the photo
	 */
	public final String getPicture() {
		return data.getString("picture");
	}

	/**
	 * The source image of the photo
	 */
	public final String getSource() {
		return data.getString("source");
	}

}
