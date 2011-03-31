package ilarkesto.integration.facebook;

import ilarkesto.core.json.JsonObject;

import java.text.SimpleDateFormat;

public class Item {

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");

	protected JsonObject data;

	public Item(JsonObject data) {
		super();
		this.data = data;
	}

	public String getString(String name) {
		return data.getString(name);
	}

	public JsonObject getJson(String name) {
		return data.getObject(name);
	}

	public final Item getItem(String name) {
		JsonObject jsonObject = data.getObject(name);
		if (jsonObject == null) return null;
		return new Item(jsonObject);
	}

	public final String getId() {
		return data.getString("id");
	}

	@Override
	public String toString() {
		return data.toString();
	}

}
