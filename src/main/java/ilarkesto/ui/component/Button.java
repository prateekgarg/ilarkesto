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
package ilarkesto.ui.component;

import ilarkesto.base.Url;

public class Button {

	private String label;
	private Url url;
	private String tooltip;
	private String icon;
	private boolean newWindow = false;

	public Button setNewWindow(boolean newWindow) {
		this.newWindow = newWindow;
		return this;
	}

	public boolean isNewWindow() {
		return newWindow;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIcon() {
		return icon;
	}

	public Button setLabel(String label) {
		this.label = label;
		return this;
	}

	public String getLabel() {
		return label;
	}

	public Button setUrl(Url url) {
		this.url = url;
		return this;
	}

	public Button put(String parameter, String value) {
		url.put(parameter, value);
		return this;
	}

	public Url getUrl() {
		return url;
	}

	public Button setTooltip(String tooltip) {
		this.tooltip = tooltip;
		return this;
	}

	public String getTooltip() {
		return tooltip;
	}

}
