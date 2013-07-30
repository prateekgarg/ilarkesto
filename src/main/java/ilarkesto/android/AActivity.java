package ilarkesto.android;

import ilarkesto.core.logging.Log;

import java.util.LinkedList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public abstract class AActivity<A extends AApp> extends Activity {

	protected Log log = Log.get(getClass());
	protected AActivity<A> context = this;

	private List<MenuItemWrapper> menuItemWrappers = new LinkedList<MenuItemWrapper>();

	@Override
	protected void onStart() {
		getApp().activityStart(this);
		super.onStart();
		if (!isHomeActivity()) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		getApp().activityStop(this);
	}

	// --- menu ---

	private Menu menu;

	protected void onCreateMenuActions() {}

	protected final <C extends AActivity<A>> MenuItem createMenuAction(AAction<C> action, int order,
			boolean showAsAction) {
		MenuItem menuItem = Android.addMenuItem(menu, order, action.getLabel(), showAsAction, action);
		MenuItemWrapper miw = new MenuItemWrapper(menuItem, action);
		menuItemWrappers.add(miw);
		miw.updateMenuItem();
		return menuItem;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		onCreateMenuActions();
		this.menu = null;
		return super.onCreateOptionsMenu(menu);
	}

	public final void updateMenuItems() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				for (MenuItemWrapper miw : menuItemWrappers) {
					miw.updateMenuItem();
				}
			}
		});
	}

	// --- ---

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				return onToolbarHomeClicked();
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	protected boolean onToolbarHomeClicked() {
		Intent intent = new Intent(this, getApp().getHomeActivity());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		return true;
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

	public void showToastShort(CharSequence text) {
		showToast(text, Toast.LENGTH_SHORT);
	}

	public void showToastShort(int textResId) {
		showToast(textResId, Toast.LENGTH_SHORT);
	}

	public void showToastLong(CharSequence text) {
		showToast(text, Toast.LENGTH_LONG);
	}

	public void showToastLong(int textResId) {
		showToast(textResId, Toast.LENGTH_LONG);
	}

	private void showToast(final int textResId, final int lenght) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(context, textResId, lenght).show();
			}
		});
	}

	private void showToast(final CharSequence text, final int lenght) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(context, text, lenght).show();
			}
		});
	}

	public Localizer getLocalizer() {
		return getApp().getLocalizer();
	}

	class MenuItemWrapper {

		private MenuItem menuItem;
		private AAction action;

		public MenuItemWrapper(MenuItem menuItem, AAction action) {
			super();
			this.menuItem = menuItem;
			this.action = action;
		}

		public void updateMenuItem() {
			menuItem.setTitle(action.getLabel());
			menuItem.setVisible(action.isVisible());
			menuItem.setEnabled(action.isEnabled());
			int iconResId = action.getIconResId();
			if (iconResId > 0) menuItem.setIcon(iconResId);
		}

	}

}
