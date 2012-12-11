package ilarkesto.android;

import android.app.Activity;

public abstract class AActivity<A extends AApp> extends Activity {

	@Override
	protected void onStart() {
		super.onStart();
		track();
	}

	@SuppressWarnings("unchecked")
	public final A getApp() {
		return (A) getApplication();
	}

	public final void track(Object... location) {
		getApp().getUserTracker().track(this, location);
	}

}
