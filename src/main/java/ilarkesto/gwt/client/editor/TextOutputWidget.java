package ilarkesto.gwt.client.editor;

import ilarkesto.gwt.client.AWidget;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class TextOutputWidget extends AWidget {

	private Label viewer;
	private AFieldModel model;

	public TextOutputWidget(AFieldModel model) {
		super();
		this.model = model;
	}

	@Override
	protected Widget onInitialization() {
		viewer = new Label();
		getElement().setId(getId());
		return viewer;
	}

	@Override
	protected void onUpdate() {
		Object value = model.getValue();
		viewer.setText(value == null ? null : String.valueOf(value));
		viewer.setTitle(getTooltip());
	}

	public String getTooltip() {
		return model.getTooltip();
	}

	@Override
	public String getId() {
		return model.getId();
	}

}
