package ilarkesto.gwt.client;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

public class AnchorPanel extends ComplexPanel {

	private AnchorElement a;

	public AnchorPanel() {
		a = AnchorElement.as(DOM.createAnchor());
		setElement(a);
		setStyleName("AnchorPanel");
	}

	@Override
	public void add(Widget w) {
		add(w, getElement());
	}

	public void setTooltip(String text) {
		a.setTitle(text);
	}

	public void setHref(String href) {
		a.setHref(href);
	}

	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}

	public void setFocus(boolean focused) {
		if (focused) {
			a.focus();
		} else {
			a.blur();
		}
	}
}
