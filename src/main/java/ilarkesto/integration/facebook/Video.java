package ilarkesto.integration.facebook;

import ilarkesto.json.JsonObject;

public class Video extends AIdentity {

	public Video(JsonObject json) {
		super(json);
	}

	/**
	 * The video title or caption
	 */
	public final String getName() {
		return json.getString("name");
	}

	/**
	 * The description of the video
	 */
	public final String getDescription() {
		return json.getString("description");
	}

	/**
	 * The URL for the thumbnail picture for the video
	 */
	public final String getPicture() {
		return json.getString("picture");
	}

	/**
	 * A URL to the raw, playable video file
	 */
	public String getSource() {
		return json.getString("source");
	}

	/**
	 * The html element that may be embedded in an Web page to play the video
	 */
	public String getEmbedHtml() {
		return json.getString("embed_html");
	}

}
