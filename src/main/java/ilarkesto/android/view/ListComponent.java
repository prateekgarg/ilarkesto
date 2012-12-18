package ilarkesto.android.view;

import ilarkesto.android.AListAdapter;
import ilarkesto.android.DataProvider;
import ilarkesto.core.logging.Log;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListComponent<I> extends AViewComponent {

	private static Log log = Log.get(ListComponent.class);

	private MyListAdapter listAdapter;
	private ListView listView;
	private ItemViewFactory<I> itemViewFactory;
	private OnItemSelectionListener<I> onSelectionListener;
	private DataProvider<List<I>> dataProvider;
	private TextView emptyText;

	public ListComponent(Context context, DataProvider<List<I>> dataProvider) {
		super(context);
		this.dataProvider = dataProvider;
	}

	@Override
	protected View buildView() {
		emptyText = Views.text(context, "Loading...");

		listAdapter = new MyListAdapter();
		new LoaderTask().execute();
		if (itemViewFactory == null) itemViewFactory = createDefaultItemViewFactory();
		if (onSelectionListener == null) onSelectionListener = createDefaultOnSelectionListener();

		listView = new ListView(context);
		listView.setEmptyView(emptyText);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
				I item = listAdapter.getItem(position);
				onSelectionListener.onItemSelected(item);
			}
		});
		return listView;
	}

	private OnItemSelectionListener<I> createDefaultOnSelectionListener() {
		return new OnItemSelectionListener<I>() {

			@Override
			public void onItemSelected(I item) {
				Toast.makeText(context, "Selected: " + item, Toast.LENGTH_SHORT).show();
			}
		};
	}

	private ItemViewFactory<I> createDefaultItemViewFactory() {
		return new ACachingItemViewFactory<I>() {

			@Override
			protected View createViewForItem(Context context, I item) {
				TextView tv = Views.text(context, item);
				tv.setPadding(5, 5, 5, 5);
				return tv;
			}
		};
	}

	public ListComponent<I> setItemViewFactory(ItemViewFactory<I> itemViewFactory) {
		this.itemViewFactory = itemViewFactory;
		return this;
	}

	public ListComponent<I> setOnSelectionListener(OnItemSelectionListener<I> onSelectionListener) {
		this.onSelectionListener = onSelectionListener;
		return this;
	}

	public static interface OnItemSelectionListener<I> {

		void onItemSelected(I item);

	}

	private class LoaderTask extends AsyncTask<Integer, Integer, List<I>> {

		@Override
		protected List<I> doInBackground(Integer... arg0) {
			List<I> items = dataProvider.getData();
			return items;
		}

		@Override
		protected void onPostExecute(List<I> items) {
			emptyText.setText("No data");
			if (items != null && items.size() > 100) listView.setFastScrollEnabled(true);
			listAdapter.setItems(items);
		}

	}

	private class MyListAdapter extends AListAdapter<I> {

		@Override
		protected View updateItemView(I item, View view) {
			return itemViewFactory.getViewForItem(context, item);
		}

	}
}
