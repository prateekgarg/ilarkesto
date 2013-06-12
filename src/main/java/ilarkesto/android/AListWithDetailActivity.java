package ilarkesto.android;

import ilarkesto.android.Swipe.OnSwipeListener;
import ilarkesto.android.view.LayoutBuilder;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public abstract class AListWithDetailActivity<I, A extends AApp> extends AListActivity<I, A> implements OnSwipeListener {

	private I selectedItem;
	private Boolean doubleView;
	private ViewGroup detailWrapper;
	private View detailViewWrapper;

	protected void onSelectedItemChanged(I selectedItem) {}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		detailWrapper = new FrameLayout(context);
	}

	@Override
	public void onListItemClick(I item) {
		selectItem(item);
	}

	@Override
	public void reloadItems() {
		selectItem(null);
		super.reloadItems();
	}

	@Override
	protected final void updateItemView(I item, View view) {
		if (item == selectedItem) {
			view.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
		} else {
			view.setBackgroundColor(Color.TRANSPARENT);
		}
		updateItemListView(item, view);
	}

	@Override
	public void onSwipeLeft() {
		if (selectedItem == null) return;
		int index = listAdapter.getIndexOf(selectedItem);
		if (index < 1) return;
		I item = listAdapter.getItem(index - 1);
		selectItem(item, Swipe.SWIPE_LEFT);
	}

	@Override
	public void onSwipeRight() {
		if (selectedItem == null) return;
		int count = listAdapter.getCount();
		if (count < 2) return;
		int index = listAdapter.getIndexOf(selectedItem);
		if (index + 1 >= count) return;
		I item = listAdapter.getItem(index + 1);
		selectItem(item, Swipe.SWIPE_RIGHT);
	}

	protected void updateItemListView(I item, View view) {
		super.updateItemView(item, view);
	}

	public void selectItem(I item) {
		selectItem(item, Swipe.SWIPE_NONE);
	}

	public void selectItem(I item, int swipeAnimationMode) {
		if (item == selectedItem) return;
		selectedItem = item;

		View detailView = null;
		if (selectedItem != null) {
			detailView = createItemDetailView(selectedItem);
			if (isDetailSwipeEnabled()) Swipe.attachOnSwipeListener(detailView, this);
		}

		if (detailView == null) {
			changeContentViewToList();
		} else {
			if (isDoubleView()) {
				if (detailViewWrapper == null || detailViewWrapper == detailWrapper) {
					detailViewWrapper = LayoutInflater.from(this).inflate(R.layout.list_with_detail, null);
					Android.addToContainer(detailViewWrapper, R.id.listContainer, createListViewWrapper());
					Android.addToContainer(detailViewWrapper, R.id.detailContainer, detailWrapper);
					if (isDetailSwipeEnabled())
						Swipe.attachOnSwipeListener(detailViewWrapper.findViewById(R.id.detailContainer), this);
				}
			} else {
				detailViewWrapper = detailWrapper;
			}

			changeContentView(detailViewWrapper);
			if (detailWrapper.getChildCount() > 0) {
				Swipe.animate(swipeAnimationMode, detailWrapper, detailWrapper.getChildAt(0), detailView);
			} else {
				detailWrapper.addView(detailView);
			}
			// listView.setSelection(listAdapter.getIndexOf(item));
		}
		onSelectedItemChanged(selectedItem);
	}

	protected boolean isDetailSwipeEnabled() {
		return false;
	}

	protected View createItemDetailView(I item) {
		if (item == null) return null;
		return LayoutBuilder.page(this, getItemTitle(item)).getView();
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
			changeContentViewToList();
			onSelectedItemChanged(selectedItem);
			return;
		}
		super.onBackPressed();
	}

	public I getSelectedItem() {
		return selectedItem;
	}

}
