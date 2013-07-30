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

import android.app.Activity;
import android.content.Context;

public class StartActivityAction extends AAction {

	private Class<? extends Activity> activity;
	private String label;

	public StartActivityAction(Context context, Class<? extends Activity> activity, String label) {
		super(context);
		this.activity = activity;
		this.label = label;
	}

	@Override
	protected void onRun() {
		Android.startActivity(context, activity);
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	protected String getTrackingIdentifier() {
		return "Start:" + activity.getSimpleName();
	}

}
