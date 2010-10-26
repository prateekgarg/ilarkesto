package ilarkesto.gwt.client.editor;

import ilarkesto.core.base.Str;

public abstract class AFieldModel<T> {

	public abstract T getValue();

	public String getTooltip() {
		return null;
	}

	public String getId() {
		return Str.getSimpleName(getClass()).replace('$', '_');
	}

}
