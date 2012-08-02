package ilarkesto.integration.facebook;

import ilarkesto.json.JsonObject;

public class Like extends AIdentity {

	public Like(JsonObject data) {
		super(data);
	}

	public String getName() {
		return json.getString("name");
	}

	public String getCategory() {
		return json.getString("category");
	}

	public Subject getSubject() {
		return createFromObject("id__loaded", Subject.class);
	}

}
