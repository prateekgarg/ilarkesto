package ilarkesto.integration.facebook;

import ilarkesto.json.JsonObject;

public class Checkin extends AIdentity {

	public Checkin(JsonObject data) {
		super(data);
	}

	public String getName() {
		return json.getString("name");
	}

	public String getCaption() {
		return json.getString("caption");
	}

	public String getIcon() {
		return json.getString("icon");
	}

	public String getPicture() {
		return json.getString("picture");
	}

}
