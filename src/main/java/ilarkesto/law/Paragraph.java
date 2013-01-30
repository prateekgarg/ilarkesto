package ilarkesto.law;

import ilarkesto.core.html.Html;
import ilarkesto.json.AJsonWrapper;
import ilarkesto.json.JsonObject;

public class Paragraph extends AJsonWrapper {

	public Paragraph(JsonObject json) {
		super(json);
	}

	public Paragraph(String html) {
		json.put("html", html);
	}

	public String getTextAsHtml() {
		return json.getString("html");
	}

	public String getTextAsString() {
		return Html.convertHtmlToText(getTextAsHtml());
	}

	@Override
	public String toString() {
		return getTextAsString();
	}

}
