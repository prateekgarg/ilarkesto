package ilarkesto.integration.facebook;

import ilarkesto.json.JsonObject;

public class Subject extends AIdentity {

	public Subject(JsonObject data) {
		super(data);
	}

	public String getName() {
		return json.getString("name");
	}

	public String getPicture() {
		return json.getString("picture");
	}

	public String getLink() {
		return json.getString("link");
	}

	public String getCategory() {
		return json.getString("category");
	}

	public String getDescription() {
		return json.getString("description");
	}

	public String getAbout() {
		return json.getString("about");
	}

	public String getWebsite() {
		return json.getString("website");
	}

	public String getBestDescription() {
		String description = getDescription();
		if (description != null) return description;

		return getAbout();
	}

}
