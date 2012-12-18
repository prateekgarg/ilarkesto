package ilarkesto.android.view;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.View;

public abstract class ACachingItemViewFactory<I> implements ItemViewFactory<I> {

	private Map<I, View> viewsByItem = new HashMap<I, View>();

	protected abstract View createViewForItem(Context context, I item);

	@Override
	public final View getViewForItem(Context context, I item) {
		View view = viewsByItem.get(item);
		if (view == null) {
			view = createViewForItem(context, item);
			viewsByItem.put(item, view);
		}
		return view;
	}

}
