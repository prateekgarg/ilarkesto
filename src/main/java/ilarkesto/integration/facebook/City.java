package ilarkesto.integration.facebook;

import ilarkesto.core.base.Str;
import ilarkesto.json.JsonObject;

public class City extends AIdentity {

	public City(JsonObject data) {
		super(data);
	}

	public final String getName() {
		return json.getString("name");
	}

	public String getNameCityOnly() {
		String name = getName();
		if (name == null) return null;
		if (name.contains(",")) return Str.cutTo(name, ",");
		return name;
	}

}
