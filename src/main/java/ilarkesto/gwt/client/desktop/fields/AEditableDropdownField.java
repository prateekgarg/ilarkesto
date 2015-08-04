/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package ilarkesto.gwt.client.desktop.fields;

import ilarkesto.core.base.Utl;
import ilarkesto.gwt.client.desktop.Widgets;

import java.util.Map;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public abstract class AEditableDropdownField extends AEditableField {

	private static final String NULL_KEY = "__NULL__";

	private ListBox listBox;

	public abstract void applyValue(String value);

	public abstract Map<String, String> createOptions();

	public abstract String getSelectedOptionKey();

	@Override
	public boolean isValueSet() {
		return getSelectedOptionKey() != null;
	}

	@Override
	public void trySubmit() throws RuntimeException {
		int selectedIndex = listBox.getSelectedIndex();
		String value = selectedIndex < 0 ? null : listBox.getValue(selectedIndex);
		if (NULL_KEY.equals(value)) value = null;
		if (value == null && isMandatory()) throw new RuntimeException("Auswahl erforderlich");
		applyValue(value);
	}

	@Override
	public IsWidget createEditorWidget() {
		listBox = new ListBox();
		listBox.getElement().setId(getId() + "_listBox");
		Style style = listBox.getElement().getStyle();
		style.setWidth(getTextBoxWidth(), Unit.PX);
		style.setPadding(Widgets.defaultSpacing, Unit.PX);

		int i = 0;
		String selectedKey = getSelectedOptionKey();
		int selectedIndex = -1;
		if (!isMandatory()) {
			listBox.addItem("", NULL_KEY);
			selectedIndex = 0;
			i++;
		}

		Map<String, String> options = createOptions();
		for (Map.Entry<String, String> entry : options.entrySet()) {
			String key = entry.getKey();
			String label = entry.getValue();
			listBox.addItem(label, key);
			if (Utl.equals(selectedKey, key)) selectedIndex = i;
			i++;
		}
		if (selectedIndex >= 0) listBox.setSelectedIndex(selectedIndex);
		if (getEditVetoMessage() == null) {} else {
			listBox.setEnabled(false);
			listBox.setTitle(getEditVetoMessage());
		}

		// listBox.addClickHandler(new ClickHandler() {
		//
		// @Override
		// public void onClick(ClickEvent event) {
		// if (fieldEditorDialogBox == null) return;
		// fieldEditorDialogBox.submit();
		// }
		// });

		if (options.size() < 3) {
			listBox.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					if (fieldEditorDialogBox == null) return;
					fieldEditorDialogBox.submit();
					// NativeEvent nat = event.getNativeEvent();
					// Log.TEST("onChange:", nat.getType(), nat.getRelatedEventTarget(),
					// nat.getCurrentEventTarget()
					// .getClass(), nat.getKeyCode(), nat.getButton(), nat.getCharCode());
				}
			});
		}

		listBox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				onSelectionChanged();
			}

		});

		listBox.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() != 13) return;
				if (fieldEditorDialogBox == null) return;
				fieldEditorDialogBox.submit();
			}
		});

		return listBox;
	}

	protected void onSelectionChanged() {}

	private int getTextBoxWidth() {
		int width = Window.getClientWidth();
		if (width > 700) width = 700;
		return width;
	}

	@Override
	public IsWidget createDisplayWidget() {
		String value = null;
		String key = getSelectedOptionKey();
		if (key != null) {
			value = createOptions().get(key);
			if (value == null) value = "[" + key + "]";
		}
		return new Label(prepareValueForDisplay(key, value));
	}

	protected String prepareValueForDisplay(String key, String value) {
		return value;
	}

}
