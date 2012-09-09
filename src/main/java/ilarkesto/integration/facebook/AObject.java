package ilarkesto.integration.facebook;

import ilarkesto.core.time.DateAndTime;
import ilarkesto.json.AJsonWrapper;
import ilarkesto.json.JsonObject;

public abstract class AObject extends AJsonWrapper {

	public AObject(JsonObject json) {
		super(json);
	}

	protected final java.util.Date getJavaDate(String name) {
		return Facebook.parseDate(json.getString(name));
	}

	protected final DateAndTime getDate(String name) {
		java.util.Date javaDate = getJavaDate(name);
		if (javaDate == null) return null;
		return new DateAndTime(javaDate);
	}

}
