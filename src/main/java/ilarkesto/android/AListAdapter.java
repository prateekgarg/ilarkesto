package ilarkesto.android;

import ilarkesto.android.view.Views;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AListAdapter<T> extends BaseAdapter {

	private List<T> items;

	@SuppressWarnings("unchecked")
	public void setItems(List<T> items) {
		this.items = items == null ? (List<T>) Collections.emptyList() : items;
		notifyDataSetChanged();
	}

	public void clear() {
		setItems(null);
	}

	protected View updateItemView(T item, View view) {
		if (view instanceof TextView) {
			((TextView) view).setText(item.toString());
		}
		return view;
	}

	protected View createItemView(T item, ViewGroup parent) {
		int itemViewId = getItemViewId(item);
		if (itemViewId == -1) {
			TextView tv = Views.text(parent.getContext(), item.toString());
			tv.setPadding(5, 5, 5, 5);
			return tv;
		}
		return ((Activity) parent.getContext()).getLayoutInflater().inflate(itemViewId, parent, false);
	}

	protected int getItemViewId(T item) {
		return -1;
	}

	@Override
	public int getCount() {
		if (items == null) return 0;
		return items.size();
	}

	@Override
	public T getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		T item = getItem(position);
		if (convertView == null) {
			convertView = createItemView(item, parent);
		}
		convertView = updateItemView(item, convertView);
		return convertView;
	}

}
