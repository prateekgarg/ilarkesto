package ilarkesto.integration.twitter;

import org.jdom2.Element;

public class TwitterUser {

	private String id;
	private String name;
	private String screenName;
	private String location;
	private String description;
	private String profileImageUrl;
	private String url;
	private boolean protectet;
	private int followersCount;

	public TwitterUser(Element e) {
		id = e.getChildText("id");
		name = e.getChildText("name");
		screenName = e.getChildText("screen_name");
		location = e.getChildText("location");
		description = e.getChildText("description");
		profileImageUrl = e.getChildText("profile_image_url");
		url = e.getChildText("url");
		protectet = Boolean.parseBoolean(e.getChildText("protected"));
		followersCount = Integer.parseInt(e.getChildText("followers_count"));
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getLocation() {
		return location;
	}

	public String getDescription() {
		return description;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public String getUrl() {
		return url;
	}

	public boolean isProtectet() {
		return protectet;
	}

	public int getFollowersCount() {
		return followersCount;
	}

	@Override
	public String toString() {
		return name;
	}

}