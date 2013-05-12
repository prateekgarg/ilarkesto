package ilarkesto.integration.facebook;

import ilarkesto.json.JsonObject;

public class Status extends AIdentity {

	public Status(JsonObject data) {
		super(data);
	}

	/**
	 * The status content
	 */
	public final String getStory() {
		return json.getString("story");
	}
}
