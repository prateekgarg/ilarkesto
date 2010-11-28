package ilarkesto.gwt.client.editor;

import ilarkesto.core.base.Str;
import ilarkesto.gwt.client.AWidget;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class TextOutputWidget extends AWidget {

	private HTML viewer;
	private AFieldModel model;
	private boolean forceEmptyChar;

	public TextOutputWidget(AFieldModel model) {
		super();
		this.model = model;
	}

	@Override
	protected Widget onInitialization() {
		viewer = new HTML();
		getElement().setId(getId());
		return viewer;
	}

	@Override
	protected void onUpdate() {
		Object value = model.getValue();
		String text = value == null ? null : String.valueOf(value);
		if (forceEmptyChar && Str.isBlank(text)) {
			viewer.setHTML("&nbsp;");
		} else {
			viewer.setText(text);
		}
		viewer.setTitle(getTooltip());
	}

	public String getTooltip() {
		return model.getTooltip();
	}

	public TextOutputWidget setForceEmptyChar(boolean forceEmptyChar) {
		this.forceEmptyChar = forceEmptyChar;
		return this;
	}

	@Override
	public String getId() {
		return model.getId();
	}

}
