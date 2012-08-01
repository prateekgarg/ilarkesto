package ilarkesto.integration.facebook;

import ilarkesto.json.JsonObject;

public class Status extends AIdentity {

	public Status(JsonObject data) {
		super(data);
	}

	/**
	 * The status message content
	 */
	public final String getMessage() {
		return json.getString("message");
	}
}
