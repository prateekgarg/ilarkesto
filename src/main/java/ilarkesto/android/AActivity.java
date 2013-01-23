package ilarkesto.android;

import ilarkesto.core.logging.Log;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

public abstract class AActivity<A extends AApp> extends Activity {

	protected Log log = Log.get(getClass());
	protected AActivity<A> context = this;

	@Override
	protected void onStart() {
		super.onStart();
		if (!isHomeActivity()) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		track();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				Intent intent = new Intent(this, getApp().getHomeActivity());
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	protected boolean isHomeActivity() {
		return false;
	}

	@SuppressWarnings("unchecked")
	public final A getApp() {
		return (A) getApplication();
	}

	protected void restart() {
		Intent intent = getIntent();
		finish();
		startActivity(intent);
	}

	@Deprecated
	// use tracker
	public final void track(Object... location) {
		getApp().getUserTracker().track(this, location);
	}

	public void showToast(CharSequence text) {
		Android.showToast(text, context);
	}

	public void showToast(int textResId) {
		Android.showToast(textResId, context);
	}

	public Localizer getLocalizer() {
		return getApp().getLocalizer();
	}

}
