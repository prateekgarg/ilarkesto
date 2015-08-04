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

import ilarkesto.core.base.Str;
import ilarkesto.gwt.client.AAction;
import ilarkesto.gwt.client.desktop.AObjectTable;
import ilarkesto.gwt.client.desktop.BuilderPanel;
import ilarkesto.gwt.client.desktop.Colors;
import ilarkesto.gwt.client.desktop.Widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class AEditableCheckboxesField extends AEditableField {

	private Table table;

	public abstract void applyValue(List<String> selectedKeys);

	public abstract Map<String, ?> createOptions();

	public abstract Collection<String> getSelectedOptionKeys();

	@Override
	public boolean isValueSet() {
		Collection<String> keys = getSelectedOptionKeys();
		return keys != null && !keys.isEmpty();
	}

	@Override
	public void trySubmit() throws RuntimeException {
		applyValue(table.getSelectedKeys());
	}

	@Override
	public IsWidget createEditorWidget() {
		table = new Table();
		return table;
	}

	private String getTextForOption(Object value) {
		return value == null ? "" : value.toString();
	}

	private void updateStyle(CheckBox checkBox) {
		Style style = checkBox.getElement().getStyle();
		if (checkBox.getValue().booleanValue()) {
			style.setColor("#000");
		} else {
			style.setColor("#666");
		}
	}

	protected boolean isHorizontal() {
		return true;
	}

	@Override
	public IsWidget createDisplayWidget() {
		return new Label(getDisplayText());
	}

	protected String getDisplayText() {
		Map<String, ?> options = createOptions();
		Collection<String> selectedKeys = getSelectedOptionKeys();
		List<String> values = new ArrayList<String>(selectedKeys.size());
		for (String key : selectedKeys) {
			values.add(getTextForOption(options.get(key)));
		}
		String delimiter = isDisplayMultiline() ? "\n" : ", ";
		String displayText = Str.concat(values, delimiter);
		return displayText;
	}

	protected boolean isDisplayMultiline() {
		return false;
	}

	class Table extends AObjectTable<Item> {

		private ArrayList<Item> items;

		public Table() {
			Collection<String> selectedKeys = getSelectedOptionKeys();
			Map<String, ?> options = createOptions();
			items = new ArrayList<Item>(options.size());
			for (Map.Entry<String, ?> entry : options.entrySet()) {
				Item item = new Item(entry.getKey(), entry.getValue());
				items.add(item);
				if (selectedKeys.contains(item.key)) item.selected = true;
			}
		}

		@Override
		protected void init(BuilderPanel wrapper) {
			super.init(wrapper);
			wrapper.createTitle("", new SelectAllAction(), new DeselectAllAction());

			add(new AColumn() {

				@Override
				public Widget getCellWidget(Item o) {
					if (!o.selected) return null;
					return Widgets.icon("checked", 32);
				}

				@Override
				protected boolean isTrimmed() {
					return true;
				}

				@Override
				public TextBox getFilterWidget() {
					return null;
				}

			});

			add(new AColumn() {

				@Override
				public Object getCellValue(Item o) {
					return getTextForOption(o.value);
				}

				@Override
				public String getColor(Item o) {
					if (o.selected) return Colors.googlePurple;
					return Colors.greyedText;
				}

			});

		}

		@Override
		protected Collection<Item> getObjects() {
			return items;
		}

		@Override
		protected void onClick(Item object, int column) {
			if (getEditVetoMessage() != null) return;
			object.selected = !object.selected;
			update();
		}

		@Override
		protected boolean isClickable() {
			return getEditVetoMessage() == null;
		}

		@Override
		protected boolean isColumnFilteringEnabled() {
			return items.size() > 42;
		}

		public List<String> getSelectedKeys() {
			ArrayList<String> ret = new ArrayList<String>();
			for (Item item : items) {
				if (item.selected) ret.add(item.key);
			}
			return ret;
		}

		class SelectAllAction extends AAction {

			@Override
			public String getLabel() {
				return "Alle auswählen";
			}

			@Override
			protected String getIconName() {
				return "select_all";
			}

			@Override
			protected void onExecute() {
				for (Item item : items) {
					item.selected = true;
				}
				Table.this.update();
			}
		}

		class DeselectAllAction extends AAction {

			@Override
			public String getLabel() {
				return "Keine auswählen";
			}

			@Override
			protected String getIconName() {
				return "deselect_all";
			}

			@Override
			protected void onExecute() {
				for (Item item : items) {
					item.selected = false;
				}
				Table.this.update();
			}

		}
	}

	class Item {

		private String key;
		private Object value;
		private boolean selected;

		public Item(String key, Object value) {
			super();
			this.key = key;
			this.value = value;
		}

	}

}
