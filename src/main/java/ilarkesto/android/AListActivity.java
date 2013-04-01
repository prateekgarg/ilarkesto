package ilarkesto.android;

import ilarkesto.android.view.Views;
import ilarkesto.core.base.Utl;

import java.util.Collections;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

public abstract class AListActivity<I, A extends AApp> extends AActivity<A> {

	protected ListView listView;
	protected View emptyView;
	protected final MyListAdapter listAdapter = new MyListAdapter();
	protected FrameLayout wrapper;

	protected abstract List<I> loadItems();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		listView = new ListView(this);
		listView.setFastScrollEnabled(true);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
				I item = listAdapter.getItem(position);
				track(item);
				AListActivity.this.onListItemClick(item);
			}
		});

		wrapper = new FrameLayout(context);
		setContentView(wrapper);

		emptyView = LayoutInflater.from(this).inflate(R.layout.list_empty, null);
		emptyView.setVisibility(View.GONE);
		wrapper.addView(emptyView);
		listView.setEmptyView(emptyView);

		wrapper.addView(listView);
	}

	protected void disableLoadIndicator() {
		emptyView.findViewById(R.id.listEmptyProgressBar).setVisibility(View.GONE);
		emptyView.findViewById(R.id.listEmptyText).setVisibility(View.VISIBLE);
	}

	protected void enableLoadIndicator() {
		emptyView.findViewById(R.id.listEmptyProgressBar).setVisibility(View.VISIBLE);
		emptyView.findViewById(R.id.listEmptyText).setVisibility(View.GONE);
	}

	protected void setEmptyListText(String emptyListText) {
		TextView tv = (TextView) emptyView.findViewById(R.id.listEmptyText);
		if (emptyListText != null) tv.setText(emptyListText);
	}

	protected void setEmptyListText(int emptyListTextResId) {
		TextView tv = (TextView) emptyView.findViewById(R.id.listEmptyText);
		tv.setText(emptyListTextResId);
	}

	@Override
	protected void onStart() {
		super.onStart();
		listAdapter.clear();
		enableLoadIndicator();
		new ItemLoader().execute();
	}

	protected void changeContentViewToList() {
		track();
		Android.removeFromParent(listView);
		changeContentView(listView);
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
		listAdapter.setItems(items);
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
		showToast(getItemTitle(item));
	}

	protected int getAdditionalItemLoadTrials() {
		return 0;
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
			long start = System.currentTimeMillis();
			List<I> items = doLoadItems();
			if (items != null && !items.isEmpty()) {
				log.info("Loading items took " + (System.currentTimeMillis() - start) + " ms. ->", items);
				return items;
			}

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
			} catch (Throwable ex) {
				log.error("Loading items failed:", ex);
				setEmptyListText(Android.text(context, R.string.loading_data_failed, Utl.getRootCauseMessage(ex)));
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
