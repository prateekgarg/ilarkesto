package ilarkesto.integration.facebook;

import ilarkesto.json.JsonObject;

public class Reference extends AIdentity {

	public Reference(JsonObject data) {
		super(data);
	}

	public String getName() {
		return json.getString("name");
	}

}
