package ilarkesto.android.view;

import ilarkesto.core.logging.Log;
import android.content.Context;
import android.view.View;

public abstract class AViewComponent {

	protected final Log log = Log.get(getClass());
	protected Context context;
	private View view;

	protected abstract View buildView();

	public AViewComponent(Context context) {
		this.context = context;
	}

	public final View getView() {
		if (view == null) view = buildView();
		return view;
	}

	protected void onUpdate() {}

	public final void update() {
		onUpdate();
	}

	protected final View createView(Object element) {
		if (element == null) return null;
		if (element instanceof View) return (View) element;
		if (element instanceof AViewComponent) return ((AViewComponent) element).buildView();
		return Views.text(context, element.getClass().getSimpleName() + ": " + element.toString());
	}

}
