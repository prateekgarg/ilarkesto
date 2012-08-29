package ilarkesto.integration.facebook;

import ilarkesto.json.JsonObject;

public class Person extends AIdentity {

	public Person(JsonObject data) {
		super(data);
	}

	public String getName() {
		return json.getString("name");
	}

	public String getFirstName() {
		return json.getString("first_name");
	}

	public String getLastName() {
		return json.getString("last_name");
	}

	public String getGender() {
		return json.getString("gender");
	}

	public boolean isMale() {
		return "male".equals(getGender());
	}

	public boolean isFemale() {
		return "female".equals(getGender());
	}

	public String getUsername() {
		return json.getString("username");
	}

	public City getHometown() {
		return createFromObject("hometown", City.class);
	}

	public City getLocation() {
		return createFromObject("location", City.class);
	}

}
