package ilarkesto.gwt.client.desktop.fields;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.ui.IsWidget;

public abstract class AGoonTextBox implements IsWidget {

	public abstract String getText();

	public abstract void setMaxLength(int maxLength);

	public abstract void setValue(String value);

	public abstract Element getElement();

	public abstract void addKeyUpHandler(KeyUpHandler enterKeyUpHandler);

	public abstract void setEnabled(boolean b);

	public abstract void setTitle(String editVetoMessage);

	public void addKeyPressHandler(KeyPressHandler numericKeyPressHandler) {}

	public void addMouseOutHandler(MouseOutHandler numericMouseOutHandler) {}

}
