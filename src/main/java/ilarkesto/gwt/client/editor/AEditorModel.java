package ilarkesto.gwt.client.editor;

import ilarkesto.gwt.client.Gwt;

public abstract class AEditorModel<T> extends AFieldModel<T> {

	public abstract void setValue(T value);

	protected void onChangeValue(T oldValue, T newValue) {}

	public boolean isEditable() {
		return true;
	}

	public void changeValue(T newValue) {
		T oldValue = getValue();
		if (Gwt.equals(oldValue, newValue)) return;
		onChangeValue(oldValue, newValue);
		setValue(newValue);
	}

}
