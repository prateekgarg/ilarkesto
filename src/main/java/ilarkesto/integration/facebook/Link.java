package ilarkesto.integration.facebook;

import ilarkesto.json.JsonObject;

public class Link extends AIdentity {

	public Link(JsonObject data) {
		super(data);
	}

	/**
	 * The URL that was shared
	 */
	public final String getLink() {
		return json.getString("link");
	}

	/**
	 * The name of the link
	 */
	public final String getName() {
		return json.getString("name");
	}

	/**
	 * A description of the link (appears beneath the link caption)
	 */
	public final String getDescription() {
		return json.getString("description");
	}

	/**
	 * A URL to the thumbnail image used in the link post
	 */
	public final String getPicture() {
		return json.getString("picture");
	}

	/**
	 * The optional message from the user about this link
	 */
	public final String getMessage() {
		return json.getString("message");
	}
}
