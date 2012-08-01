package ilarkesto.integration.facebook;

import ilarkesto.json.JsonObject;

public class Video extends FeedItem {

	public Video(JsonObject data) {
		super(data);
	}

	/**
	 * The video title or caption
	 */
	public final String getName() {
		return data.getString("name");
	}

	/**
	 * The description of the video
	 */
	public final String getDescription() {
		return data.getString("description");
	}

	/**
	 * The URL for the thumbnail picture for the video
	 */
	public final String getPicture() {
		return data.getString("picture");
	}

	/**
	 * A URL to the raw, playable video file
	 */
	public String getSource() {
		return data.getString("source");
	}

	/**
	 * The html element that may be embedded in an Web page to play the video
	 */
	public String getEmbedHtml() {
		return data.getString("embed_html");
	}

}
