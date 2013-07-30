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

import ilarkesto.core.base.Str;
import ilarkesto.core.logging.Log;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public abstract class AAction<C extends Context> implements Runnable, OnClickListener {

	protected Log log = Log.get(getClass());

	protected C context;

	public AAction(C context) {
		this.context = context;
	}

	protected void onRun() {
		Toast.makeText(context, getLabel() + " action triggered! But where is the implementation?", Toast.LENGTH_LONG)
				.show();
	}

	public String getLabel() {
		int resId = getLabelResId();
		if (resId >= 0) return Android.text(context, resId);
		return getClass().getName();
	}

	public int getLabelResId() {
		return -1;
	}

	@Override
	public final void run() {
		AAndroidTracker.get().action(getTrackingIdentifier(), getLabel());
		if (isInternetRequired()) {
			if (!Android.isOnline(context)) {
				AAndroidTracker.get().userProblem("NoInternetForAction", getTrackingIdentifier());
				log.debug("Internet required, but not available");
				showError("Internet benötigt und zur Zeit nicht verfügbar.");
				return;
			}
		}
		try {
			onRun();
		} catch (Exception ex) {
			AAndroidTracker.get().exception("ActionFailed:" + getTrackingIdentifier(), ex);
			log.error(ex);
			throw new RuntimeException("Executing action failed: " + getClass().getSimpleName(), ex);
		}
	}

	@Override
	public void onClick(View v) {
		run();
	}

	protected String getTrackingIdentifier() {
		return Str.removeSuffix(getClass().getSimpleName(), "Action");
	}

	protected void showError(String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	protected boolean isInternetRequired() {
		return false;
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isVisible() {
		return true;
	}

	public int getIconResId() {
		return -1;
	}

}
