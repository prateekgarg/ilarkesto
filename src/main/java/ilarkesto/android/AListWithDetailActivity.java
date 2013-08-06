package ilarkesto.android;

import ilarkesto.android.Swipe.OnSwipeListener;
import ilarkesto.android.view.LayoutBuilder;

import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public abstract class AListWithDetailActivity<I, A extends AApp> extends AListActivity<I, A> implements OnSwipeListener {

	private I selectedItem;
	private Boolean doubleView;
	private ViewGroup detailWrapper;
	private View detailViewWrapper;
	private boolean itemPreselected;

	protected void onSelectedItemChanged(I selectedItem) {}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		detailWrapper = new FrameLayout(context);
	}

	@Override
	protected boolean onToolbarHomeClicked() {
		if (getSelectedItem() != null && !isDoubleView()) {
			selectItem(null);
			return true;
		}
		return super.onToolbarHomeClicked();
	}

	@Override
	protected void onItemsLoaded(List<I> items) {
		super.onItemsLoaded(items);

		I preselectedItem = getPreselectedItem(items);
		if (preselectedItem != null) {
			itemPreselected = true;
			selectItem(preselectedItem);
		}

		updateMenuItems();
	}

	protected I getPreselectedItem(List<I> items) {
		return null;
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
		int index = getIndexOfSelectedItem();
		if (index < 1) return;
		I item = listAdapter.getItem(index - 1);
		selectItem(item, Swipe.SWIPE_LEFT);
	}

	public int getIndexOfSelectedItem() {
		return listAdapter.getIndexOf(selectedItem);
	}

	@Override
	public void onSwipeRight() {
		if (selectedItem == null) return;
		int count = listAdapter.getCount();
		if (count < 2) return;
		int index = getIndexOfSelectedItem();
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

		updateView(swipeAnimationMode);
	}

	public void updateView() {
		updateView(Swipe.SWIPE_NONE);
	}

	private void updateView(int swipeAnimationMode) {
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
		updateMenuItems();
		if (selectedItem != null) AAndroidTracker.get().trackObjectView(selectedItem);
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
		if (isDoubleView() || itemPreselected) {
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

	public final I getSelectedItem() {
		return selectedItem;
	}

	public final boolean isFirstItemSelected() {
		return getIndexOfSelectedItem() == 0;
	}

	public final boolean isLastItemSelected() {
		return getIndexOfSelectedItem() == getItemCount() - 1;
	}

	public OnClickListener createSwipeLeftOnClickListener() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				onSwipeLeft();
			}
		};
	}

	public OnClickListener createSwipeRightOnClickListener() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				onSwipeRight();
			}
		};
	}

}
