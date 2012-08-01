package ilarkesto.integration.facebook;

import ilarkesto.json.AJsonWrapper;
import ilarkesto.json.JsonObject;

import java.util.Date;

public abstract class AObject extends AJsonWrapper {

	public AObject(JsonObject json) {
		super(json);
	}

	protected final Date getDate(String name) {
		return Facebook.parseDate(json.getString(name));
	}

}
