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

import ilarkesto.core.base.OperationObserver;
import android.app.Activity;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;

public abstract class ALazyView<A extends Activity, R> extends AView<A> implements OperationObserver {

	protected abstract R doInBackground(OperationObserver observer);

	protected abstract View createView(R result);

	public ALazyView(A context) {
		super(context);
		ProgressBar pb = new ProgressBar(context);
		setView(pb, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER));
		Android.start(new Task());
	}

	@Override
	public void onOperationInfoChanged(String key, Object... arguments) {
		// TODO
	}

	@Override
	public boolean isAbortRequested() {
		return false;
	}

	class Task extends AsyncTask<Object, Object, R> {

		@Override
		protected R doInBackground(Object... params) {
			return ALazyView.this.doInBackground(ALazyView.this);
		}

		@Override
		protected void onPostExecute(R result) {
			setView(createView(result));
		}

	}

}
