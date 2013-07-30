package ilarkesto.android;

import ilarkesto.android.view.Views;
import ilarkesto.core.base.OperationObserver;
import ilarkesto.core.base.RuntimeTracker;
import ilarkesto.core.base.Utl;

import java.util.Collections;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public abstract class AListActivity<I, A extends AApp> extends AActivity<A> implements OperationObserver {

	protected ListView listView;
	protected View emptyView;
	protected final MyListAdapter listAdapter = new MyListAdapter();
	protected FrameLayout wrapper;

	protected abstract List<I> loadItems();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		listView = new ListView(this);
		// View listHeaderView = createListHeaderView();
		// if (listHeaderView != null) listView.addHeaderView(listHeaderView);
		View listFooterView = createListFooterView();
		if (listFooterView != null) listView.addFooterView(listFooterView);
		listView.setBackgroundColor(context.getResources().getColor(R.color.list_bg));
		listView.setFastScrollEnabled(true);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
				if (position < 0 || position >= getItemCount()) return;
				I item = listAdapter.getItem(position);
				AListActivity.this.onListItemClick(item);
			}
		});

		wrapper = new FrameLayout(context);
		setContentView(wrapper, Views.lpMatch());

		emptyView = LayoutInflater.from(this).inflate(R.layout.list_empty, null);
		emptyView.setVisibility(View.GONE);
		wrapper.addView(emptyView, Views.lpMatch());
		listView.setEmptyView(emptyView);

		wrapper.addView(createListViewWrapper(), Views.lpMatch());
	}

	// protected View createListHeaderView() {
	// return null;
	// }

	protected View createListFooterView() {
		return null;
	}

	protected View createListViewWrapper() {
		Android.removeFromParent(listView);
		if (true) return listView;
		LinearLayout layout = Views.horizontal(context);
		LayoutParams lp = new LayoutParams(600, LayoutParams.FILL_PARENT);
		lp.gravity = Gravity.CENTER_HORIZONTAL;
		layout.addView(listView, lp);
		return layout;
	}

	protected void disableLoadIndicator() {
		emptyView.findViewById(R.id.listEmptyProgressBar).setVisibility(View.GONE);
		emptyView.findViewById(R.id.listEmptyText).setVisibility(View.VISIBLE);
	}

	protected void enableLoadIndicator() {
		emptyView.findViewById(R.id.listEmptyProgressBar).setVisibility(View.VISIBLE);
		emptyView.findViewById(R.id.listEmptyText).setVisibility(View.GONE);
	}

	public final void setEmptyListText(String emptyListText) {
		TextView tv = (TextView) emptyView.findViewById(R.id.listEmptyText);
		if (emptyListText != null) tv.setText(emptyListText);
	}

	public final void setEmptyListText(int emptyListTextResId) {
		TextView tv = (TextView) emptyView.findViewById(R.id.listEmptyText);
		tv.setText(emptyListTextResId);
	}

	public final void setLoadIndicatorText(final String text) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				log.info("Updating load indicator text:", text);
				TextView tv = (TextView) emptyView.findViewById(R.id.listEmptyText);
				tv.setText(text);
				if (text != null)
					emptyView.findViewById(R.id.listEmptyText).setVisibility(
						emptyView.findViewById(R.id.listEmptyProgressBar).getVisibility());
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();
		listAdapter.clear();
		enableLoadIndicator();
		Android.start(new ItemLoader());
	}

	public void reloadItems() {
		listAdapter.clear();
		enableLoadIndicator();
		new ItemLoader().execute();
	}

	protected void changeContentViewToList() {
		changeContentView(createListViewWrapper());
	}

	protected void changeContentView(View view) {
		Android.removeFromParent(view);
		if (wrapper.getChildCount() > 0) {
			if (wrapper.getChildAt(0) == view) return;
			wrapper.removeAllViews();
		}
		if (view != null) wrapper.addView(view);
	}

	protected void onItemsLoaded(List<I> items) {
		disableLoadIndicator();
		listAdapter.addItems(items);
		updateMenuItems();
	}

	public List<I> getItems() {
		return listAdapter.getItems();
	}

	public void addItems(final List<I> items) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				disableLoadIndicator();
				listAdapter.addItems(items);
			}
		});
	}

	public void addItem(final I item) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				disableLoadIndicator();
				listAdapter.addItem(item);
			}
		});
	}

	protected void updateItemView(I item, View view) {
		Views.updateTextView(view, R.id.label, getItemTitle(item));
	}

	protected String getItemTitle(I item) {
		return item.toString();
	}

	protected int getItemLayoutResId(I item) {
		return R.layout.listitem_justlabel;
	}

	public void onListItemClick(I item) {
		showToastShort(getItemTitle(item));
	}

	protected int getAdditionalItemLoadTrials() {
		return 0;
	}

	public final int getItemCount() {
		return listAdapter.getCount();
	}

	@Override
	public void onOperationInfoChanged(String key, Object... arguments) {
		String text = Android.text(context, "ListOperation" + key, arguments);
		setLoadIndicatorText(text);
	}

	@Override
	public boolean isAbortRequested() {
		return false;
	}

	class MyListAdapter extends AListAdapter<I> {

		@Override
		protected View updateItemView(I item, View view) {
			AListActivity.this.updateItemView(item, view);
			return view;
		}

		@Override
		protected int getItemViewId(I item) {
			return AListActivity.this.getItemLayoutResId(item);
		}

	}

	class ItemLoader extends AsyncTask<Object, Integer, List<I>> {

		@Override
		protected List<I> doInBackground(Object... params) {
			RuntimeTracker rt = new RuntimeTracker();
			List<I> ret = doLoadItemsMultitry();
			log.info("Items loaded:", rt.getRuntimeFormated(), "->", ret);
			AAndroidTracker.get().listLoadTime(rt.getRuntime(), AListActivity.this.getClass().getSimpleName(),
				ret.size() + " items");
			return ret;
		}

		private List<I> doLoadItemsMultitry() {
			List<I> items = doLoadItems();
			if (items != null && !items.isEmpty()) { return items; }

			int additionalLoadTrials = getAdditionalItemLoadTrials();
			for (int i = 0; i < additionalLoadTrials; i++) {
				sleep(i * 1000);
				items = doLoadItems();
				if (items != null && !items.isEmpty()) break;
			}

			return items;
		}

		private List<I> doLoadItems() {
			try {
				return loadItems();
			} catch (final Throwable ex) {
				log.error("Loading items failed:", ex);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						setEmptyListText(Android.text(context, R.string.loading_data_failed,
							Utl.getRootCauseMessage(ex)));
					}
				});
				return Collections.emptyList();
			}
		}

		private void sleep(int count) {
			long time = count > 3 ? 3000 : 1000 * count;
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				log.info("ItemLoader interrupted.", e);
				throw new RuntimeException(e);
			}
		}

		@Override
		protected void onPostExecute(List<I> result) {
			onItemsLoaded(result);
		}

	}

}
