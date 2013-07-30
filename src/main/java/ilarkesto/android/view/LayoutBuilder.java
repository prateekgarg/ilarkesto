package ilarkesto.android.view;

import ilarkesto.android.Android;
import ilarkesto.android.R;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class LayoutBuilder extends AViewComponent {

	private final List<Object> elements = new ArrayList<Object>();

	private int orientation = LinearLayout.VERTICAL;
	private int spacing = 0;
	private int padding = 0;
	private int color = Color.TRANSPARENT;
	private boolean scroller = false;

	public LayoutBuilder(Context context) {
		super(context);
	}

	public static LayoutBuilder page(Context context, String title) {
		return page(context, Views.title(context, title));
	}

	public static LayoutBuilder page(Context context, View title) {
		return page(context).add(title);
	}

	public static LayoutBuilder page(Context context) {
		return new LayoutBuilder(context).setScroller(true);
	}

	@Override
	protected View buildView() {
		LinearLayout ll = new LinearLayout(context);
		ll.setWeightSum(1);
		ll.setOrientation(orientation);
		if (padding > 0) ll.setPadding(padding, padding, padding, padding);

		if (color != Color.TRANSPARENT) ll.setBackgroundColor(color);

		List<View> views = new ArrayList<View>(elements.size());
		for (Object element : elements) {
			View view = createView(element);
			if (view == null) continue;
			views.add(view);
		}

		boolean first = true;
		for (View view : views) {
			if (first) {
				first = false;
			} else {
				Views.addSpacer(ll, spacing);
			}

			LayoutParams lp;
			if (orientation == LinearLayout.HORIZONTAL) {
				float weight = 1 / (float) views.size();
				lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, weight);
			} else {
				lp = Views.lp();
			}
			ll.addView(view, lp);
		}

		if (scroller) return Views.scroller(ll);
		return ll;
	}

	public LayoutBuilder add(Object... elements) {
		if (elements == null) return this;
		for (Object element : elements) {
			if (element == null) continue;
			this.elements.add(element);
		}
		return this;
	}

	public LayoutBuilder createVertical() {
		LayoutBuilder lb = new LayoutBuilder(context);
		lb.setSpacing(spacing); // inherit spacing
		add(lb);
		return lb;
	}

	public LayoutBuilder createHorizontal() {
		return createVertical().setHorizontal();
	}

	public LayoutBuilder setColor(int color) {
		this.color = color;
		return this;
	}

	public LayoutBuilder createPanelsGroup() {
		LayoutBuilder pg = createVertical();
		pg.setSpacing(Views.DEFAULT_PADDING);
		pg.setPadding(Views.DEFAULT_PADDING);
		pg.setColor(context.getResources().getColor(R.color.panelgroup_bg));
		return pg;
	}

	public Split splitInTwoForPanels(Context context) {
		return splitInTwo(Android.isScreenWidthForTwoPanels(context));
	}

	public Split splitInTwoForButtons(Context context) {
		return splitInTwo(Android.isScreenWidthForTwoButtons(context));
	}

	/**
	 * @param realSplit true to really split, false to not split
	 */
	public Split splitInTwo(boolean realSplit) {
		Split split = new Split();
		LayoutBuilder horizontal = realSplit ? createHorizontal() : null;
		split.a = realSplit ? horizontal.createVertical() : this;
		split.b = realSplit ? horizontal.createVertical() : this;
		return split;
	}

	/**
	 * @param realSplit true to really split, false to not split
	 * @param allFour true to really split in 4, false to split only in two
	 */
	public Split splitInFour(boolean realSplit, boolean allFour) {
		Split split = new Split();
		LayoutBuilder horizontal = realSplit ? createHorizontal() : null;
		split.a = realSplit ? horizontal.createVertical() : this;
		split.b = realSplit ? horizontal.createVertical() : this;
		split.c = realSplit ? (allFour ? horizontal.createVertical() : split.a) : this;
		split.d = realSplit ? (allFour ? horizontal.createVertical() : split.b) : this;
		return split;
	}

	public PanelBuilder createPanel() {
		PanelBuilder panel = new PanelBuilder(context);
		add(panel);
		return panel;
	}

	public PanelBuilder createPanel(CharSequence title) {
		return createPanel().setTitle(title);
	}

	public LayoutBuilder setOrientation(int orientation) {
		this.orientation = orientation;
		return this;
	}

	public LayoutBuilder setScroller(boolean scroller) {
		this.scroller = scroller;
		return this;
	}

	public LayoutBuilder setHorizontal() {
		return setOrientation(LinearLayout.HORIZONTAL);
	}

	public LayoutBuilder setSpacing(int spacing) {
		this.spacing = spacing;
		return this;
	}

	public LayoutBuilder setPadding(int padding) {
		this.padding = padding;
		return this;
	}

	public int getElementsCount() {
		if (elements == null) return 0;
		return elements.size();
	}

	public boolean isEmpty() {
		if (elements == null) return true;
		return elements.isEmpty();
	}

	public static class Split {

		public LayoutBuilder a;
		public LayoutBuilder b;
		public LayoutBuilder c;
		public LayoutBuilder d;

	}

}
