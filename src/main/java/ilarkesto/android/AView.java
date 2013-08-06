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
import android.view.View;
import android.widget.FrameLayout;

public abstract class AView<A extends Activity> extends FrameLayout {

	protected final A context;

	public AView(A context) {
		super(context);
		this.context = context;
	}

	public final void setView(View view) {
		setView(view, null);
	}

	public final void setView(View view, LayoutParams lp) {
		removeAllViews();
		if (view != null) {
			if (lp == null) lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			addView(view, lp);
		}
	}

}
