package ilarkesto.law;

import ilarkesto.json.AJsonWrapper;
import ilarkesto.json.JsonObject;

public final class BookRef extends AJsonWrapper {

	public BookRef(String code, String title) {
		super();
		json.put("code", code);
		json.put("title", title);
	}

	public BookRef(JsonObject json) {
		super(json);
	}

	public String getCode() {
		return json.getString("code");
	}

	public boolean isCode(String code) {
		if (code == null) return false;
		return getCode().equalsIgnoreCase(code);
	}

	public String getTitle() {
		return json.getString("title");
	}

	@Override
	public String toString() {
		return getCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BookRef)) return false;
		return getCode().equals(((BookRef) obj).getCode());
	}

	@Override
	public int hashCode() {
		return getCode().hashCode();
	}

}
