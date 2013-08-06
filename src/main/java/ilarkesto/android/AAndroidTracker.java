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
package ilarkesto.android;

import ilarkesto.core.logging.Log;
import ilarkesto.core.tracking.ATracker;
import android.app.Activity;

public abstract class AAndroidTracker extends ATracker {

	static AAndroidTracker instance;

	public abstract void activityStart(Activity activity);

	public abstract void activityStop(Activity activity);

	public static AAndroidTracker get() {
		return instance;
	}

	public static final AAndroidTracker DUMMY = new AAndroidTracker() {

		private final Log log = Log.get("DummyTracker");

		@Override
		public void timing(String category, long interval, String name, String label) {
			log.info("timing", "->", category, "->", interval, "->", name, "->", label);
		}

		@Override
		public void exception(String description, Exception ex) {
			log.info("exception", "->", description, "->", ex);
		}

		@Override
		public void event(String category, String event, String label) {
			log.info("event", "->", category, "->", event, "->", label);
		}

		@Override
		public void activityStart(Activity activity) {
			log.info("activity_start", activity.getClass().getSimpleName());
		}

		@Override
		public void activityStop(Activity activity) {
			log.info("activity_stop", activity.getClass().getSimpleName());
		}

	};

}
