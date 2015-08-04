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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ActionsMenuAction extends AAction {

	private List<AAction> actions = new ArrayList<AAction>();

	public ActionsMenuAction(AAction... actions) {
		addActions(actions);
	}

	public void addActions(AAction... actions) {
		for (AAction action : actions) {
			this.actions.add(action);
		}
	}

	public void addActions(Collection<AAction> actions) {
		this.actions.addAll(actions);
	}

	@Override
	public String getLabel() {
		return "Funktionen";
	}

	@Override
	protected void onExecute() {
		new ActionSelectionDialogBox(actions).setPopupPositionAndShow(getClickEvent());
	}

	@Override
	protected String getIconName() {
		return "menu";
	}

}
