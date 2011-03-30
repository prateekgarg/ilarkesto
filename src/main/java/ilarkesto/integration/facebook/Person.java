package ilarkesto.integration.facebook;

import ilarkesto.core.json.JsonObject;

public class Person extends Item {

	public Person(JsonObject data) {
		super(data);
	}

	public String getName() {
		return data.getString("name");
	}

	public String getUsername() {
		return data.getString("username");
	}

}
