package ilarkesto.android;

import ilarkesto.android.view.LayoutBuilder;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;

public abstract class AListWithDetailActivity<I, A extends AApp> extends AListActivity<I, A> {

	private I selectedItem;
	private Boolean doubleView;

	protected void onSelectedItemChanged(I selectedItem) {}

	@Override
	public void onListItemClick(I item) {
		showItemDetail(item);
	}

	@Override
	protected final void updateItemView(I item, View view) {
		if (item == selectedItem) {
			view.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
		} else {
			view.setBackgroundColor(Color.TRANSPARENT);
		}
		updateItemListView(item, view);
	}

	protected void updateItemListView(I item, View view) {
		super.updateItemView(item, view);
	}

	public void showItemDetail(I item) {
		View detailView = createItemDetailView(item);
		if (detailView == null) return;
		selectedItem = item;
		changeContentView(createDetailWrapper(detailView));
		onSelectedItemChanged(selectedItem);
	}

	protected View createItemDetailView(I item) {
		return LayoutBuilder.page(this, getItemTitle(item)).getView();
	}

	private View createDetailWrapper(View detailView) {
		if (!isDoubleView()) return detailView;

		View listWithDetail = LayoutInflater.from(this).inflate(R.layout.list_with_detail, null);
		Android.addToContainer(listWithDetail, R.id.listContainer, listView);
		Android.addToContainer(listWithDetail, R.id.detailContainer, detailView);
		return listWithDetail;
	}

	public final boolean isDoubleView() {
		if (doubleView == null) doubleView = determineDoubleView();
		return doubleView;
	}

	protected boolean determineDoubleView() {
		return Android.isTabletDevice(this) && Android.isOrientationLandscape(this);
	}

	@Override
	public void onBackPressed() {
		if (isDoubleView()) {
			super.onBackPressed();
			return;
		}

		if (selectedItem != null) {
			selectedItem = null;
			setContentViewToList();
			onSelectedItemChanged(selectedItem);
			return;
		}
		super.onBackPressed();
	}

	public I getSelectedItem() {
		return selectedItem;
	}

}
