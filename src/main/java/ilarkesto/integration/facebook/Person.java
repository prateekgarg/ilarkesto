package ilarkesto.integration.facebook;

import ilarkesto.json.JsonObject;

public class Person extends AIdentity {

	public Person(JsonObject data) {
		super(data);
	}

	public String getName() {
		return json.getString("name");
	}

	public String getUsername() {
		return json.getString("username");
	}

}
