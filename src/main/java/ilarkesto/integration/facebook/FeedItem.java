package ilarkesto.integration.facebook;

import ilarkesto.json.JsonObject;

import java.util.Date;

public class FeedItem extends Item {

	public FeedItem(JsonObject data) {
		super(data);
	}

	public final Person getFrom() {
		if (!data.contains("from")) return null;
		return new Person(data.getObject("from"));
	}

	private Person getTo() {
		if (!data.contains("to")) return null;
		return new Person(data.getObject("to"));
	}

	public final boolean isFromFeedOwner() {
		return getTo() == null;
	}

	public final Date getCreatedTime() {
		return getDataDate("created_time");
	}

	public final Date getUpdatedTime() {
		return getDataDate("updated_time");
	}

	public final String getType() {
		return data.getString("type");
	}

	public final boolean isTypePhoto() {
		return "photo".equals(getType());
	}

	private Date getDataDate(String name) {
		String s = getString(name);
		if (s == null) return null;
		try {
			return DATE_FORMAT.parse(s);
		} catch (java.text.ParseException ex) {
			throw new RuntimeException("Parsing date with format \"" + DATE_FORMAT.toString() + "\" failed: " + s, ex);
		}
	}

}
