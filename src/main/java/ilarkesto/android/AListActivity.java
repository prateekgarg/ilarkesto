package ilarkesto.android;

import ilarkesto.android.view.Views;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public abstract class AListActivity<I, A extends AApp> extends AActivity<A> {

	protected ListView listView;
	private final MyListAdapter listAdapter = new MyListAdapter();
	protected ViewGroup wrapper;

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

		wrapper = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.animator, null);
		setContentView(wrapper);

		View emptyView = LayoutInflater.from(this).inflate(R.layout.list_empty, null);
		emptyView.setVisibility(View.GONE);
		wrapper.addView(emptyView);
		listView.setEmptyView(emptyView);

		wrapper.addView(listView);

	}

	@Override
	protected void onStart() {
		super.onStart();
		listAdapter.clear();
		new ItemLoader().execute();
	}

	protected void setContentViewToList() {
		track();
		Android.removeFromParent(listView);
		changeContentView(listView);
	}

	protected void changeContentView(View view) {
		Android.removeFromParent(view);
		wrapper.removeAllViews();
		wrapper.addView(view);
	}

	protected void onItemsLoaded(List<I> items) {
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
			List<I> items = loadItems();
			if (items != null && !items.isEmpty()) {
				log.info("Loading items took " + (System.currentTimeMillis() - start) + " ms. ->", items);
				return items;
			}
			for (int i = 0; i < 42; i++) {
				sleep(i);
				items = loadItems();
				if (!items.isEmpty()) break;
			}

			return items;
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
