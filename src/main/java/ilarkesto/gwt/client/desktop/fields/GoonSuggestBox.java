package ilarkesto.gwt.client.desktop.fields;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.Widget;

public class GoonSuggestBox extends AGoonTextBox {

	private SuggestBox suggestBox;

	public GoonSuggestBox(List<String> suggestions) {
		suggestBox = new SuggestBox(getSuggestOracle(suggestions));
	}

	public static SuggestOracle getSuggestOracle(final List<String> suggestValues) {
		return new SuggestOracle() {

			@Override
			public void requestSuggestions(Request request, Callback callback) {
				final String query = request.getQuery();

				final Set<SuggestOracle.Suggestion> suggestions = new HashSet<SuggestOracle.Suggestion>();

				for (final String value : suggestValues) {
					if (value == null) continue;
					if (value.toLowerCase().contains(query.toLowerCase())) {
						suggestions.add(new Suggestion() {

							@Override
							public String getReplacementString() {
								return value;
							}

							@Override
							public String getDisplayString() {
								return value;
							}

						});
					}
				}

				callback.onSuggestionsReady(request, new Response(suggestions));
			}
		};
	}

	@Override
	public Widget asWidget() {
		return suggestBox.asWidget();
	}

	@Override
	public String getText() {
		return suggestBox.getText();
	}

	@Override
	public void setMaxLength(int maxLength) {
		// TODO gibts nicht.
	}

	@Override
	public void setValue(String value) {
		suggestBox.setValue(value);
	}

	@Override
	public Element getElement() {
		return suggestBox.getElement();
	}

	@Override
	public void addKeyUpHandler(KeyUpHandler enterKeyUpHandler) {
		suggestBox.addKeyUpHandler(enterKeyUpHandler);
	}

	@Override
	public void setEnabled(boolean b) {
		suggestBox.setEnabled(b);
	}

	@Override
	public void setTitle(String editVetoMessage) {
		suggestBox.setTitle(editVetoMessage);
	}

}
