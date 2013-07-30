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
package ilarkesto.core.tracking;

public abstract class ATracker {

	public abstract void event(String category, String event, String label);

	public abstract void exception(String description, Exception ex);

	public abstract void timing(String category, long interval, String name, String label);

	public void payment(String productId, String label) {
		event("payment", productId, label);
	}

	public void preferenceChange(String preference, String value) {
		event("preference_change", preference, value);
	}

	public void downloadTime(long interval, String name, String label) {
		timing("download_time", interval, name, label);
	}

	public void listLoadTime(long interval, String name, String label) {
		timing("list_load_time", interval, name, label);
	}

	public void userProblem(String problemId, String description) {
		event("user_problem", problemId, description);
	}

	public void userDecision(String decisionId, String decision) {
		event("user_decision", decisionId, decision);
	}

	public void action(String actionId, String actionLabel) {
		event("action", actionId, actionLabel);
	}

	public void objectView(String objectType, String object) {
		event("object_view", objectType, object);
	}

	public void trackObjectView(Object object) {
		if (object == null) return;
		objectView(object.getClass().getSimpleName(), object.toString());
	}

}
