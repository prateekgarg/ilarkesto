package ilarkesto.gwt.client.desktop;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.DialogBox;

public class ExtendedDialogBox extends DialogBox {

	public ExtendedDialogBox(boolean autoHide, boolean modal) {
		super(autoHide, modal);
	}

	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent event) {
		super.onPreviewNativeEvent(event);
		if (isAutoHideEnabled() && event.getTypeInt() == Event.ONKEYDOWN
				&& event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) hide();
	}
}