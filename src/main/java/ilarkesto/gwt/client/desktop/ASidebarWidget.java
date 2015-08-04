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
package ilarkesto.gwt.client.desktop;

import ilarkesto.gwt.client.AAction;
import ilarkesto.gwt.client.Updatable;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ASidebarWidget implements IsWidget, Updatable {

	private static final int WIDTH = 180;

	private FlowPanel container = new FlowPanel();
	private SimplePanel wrapper = new SimplePanel(container);

	@Override
	public final Updatable update() {
		container.clear();
		container.getElement().getStyle().setWidth(WIDTH - (Widgets.defaultSpacing * 2), Unit.PX);
		container.getElement().getStyle().setHeight(100, Unit.PCT);
		container.getElement().getStyle().setPosition(Position.FIXED);
		container.getElement().getStyle().setPadding(Widgets.defaultSpacing, Unit.PX);

		wrapper.getElement().setId("SidebarWidget");
		Style style = wrapper.getElement().getStyle();
		style.setWidth(WIDTH, Unit.PX);
		style.setHeight(100, Unit.PCT);
		style.setDisplay(Display.NONE);

		onUpdate();
		return this;
	}

	protected void onUpdate() {}

	protected void add(AAction action) {
		ActionButton button = new ActionButton(action).setWidth100();
		SimplePanel buttonWrapper = new SimplePanel(button.asWidget());
		container.add(buttonWrapper);
		container.add(Widgets.verticalSpacer());
	}

	protected void addSpacer() {
		container.add(Widgets.verticalSpacer(Widgets.defaultSpacing * 3));
	}

	@Override
	public Widget asWidget() {
		return wrapper;
	}

	public void toggle() {
		if (Style.Display.BLOCK.getCssName().equals(wrapper.getElement().getStyle().getDisplay())) {
			hide();
		} else {
			show();
		}
	}

	public void show() {
		update();
		wrapper.getElement().getStyle().setDisplay(Display.BLOCK);
	}

	public void hide() {
		wrapper.getElement().getStyle().setDisplay(Display.NONE);
	}

}
