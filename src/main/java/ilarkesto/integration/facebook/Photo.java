package ilarkesto.integration.facebook;

import ilarkesto.json.JsonObject;

public class Photo extends AIdentity {

	public Photo(JsonObject data) {
		super(data);
	}

	/**
	 * The user provided caption given to this photo
	 */
	public final String getName() {
		return json.getString("name");
	}

	/**
	 * A link to the photo on Facebook
	 */
	public final String getLink() {
		return json.getString("link");
	}

	/**
	 * The thumbnail-sized source of the photo
	 */
	public final String getPicture() {
		return json.getString("picture");
	}

	/**
	 * The source image of the photo
	 */
	public final String getSource() {
		return json.getString("source");
	}

}
