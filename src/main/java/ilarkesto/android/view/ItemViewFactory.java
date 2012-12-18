package ilarkesto.android.view;

import android.content.Context;
import android.view.View;

public interface ItemViewFactory<I> {

	View getViewForItem(Context context, I item);

}
