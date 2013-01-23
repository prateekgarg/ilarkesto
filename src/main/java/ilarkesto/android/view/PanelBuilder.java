package ilarkesto.android.view;

import ilarkesto.android.R;
import ilarkesto.core.base.Str;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnHoverListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PanelBuilder extends AViewComponent {

	private boolean contentPadding = true;
	private View title;
	private Object content;
	private int contentHeight = LayoutParams.WRAP_CONTENT;
	private int contentWidth = LayoutParams.MATCH_PARENT;
	private OnClickListener onClickListener;

	public PanelBuilder(Context context) {
		super(context);
	}

	@Override
	protected View buildView() {
		if (content == null) return null;

		LinearLayout ll = new LinearLayout(context);
		ll.setOrientation(LinearLayout.VERTICAL);
		if (onClickListener == null) {
			ll.setBackgroundColor(context.getResources().getColor(R.color.panel_content_bg));
		} else {
			ll.setFocusable(true);
			ll.setClickable(true);
			final int hoverColor = context.getResources().getColor(android.R.color.holo_blue_light);
			ll.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					boolean entered = false;
					boolean left = false;
					switch (event.getAction()) {
						case MotionEvent.ACTION_MOVE:
							entered = true;
							break;
						case MotionEvent.ACTION_UP:
						case MotionEvent.ACTION_CANCEL:
							left = true;
							break;
					}
					if (entered) v.setBackgroundColor(hoverColor);
					if (left) v.setBackgroundResource(R.drawable.panel_with_link_bg);
					return false;
				}
			});
			ll.setOnHoverListener(new OnHoverListener() {

				@Override
				public boolean onHover(View v, MotionEvent event) {
					return false;
				}
			});
			ll.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						v.setBackgroundColor(hoverColor);
					} else {
						v.setBackgroundResource(R.drawable.panel_with_link_bg);
					}
				}
			});
			ll.setOnClickListener(onClickListener);
			ll.setBackgroundResource(R.drawable.panel_with_link_bg);
		}

		if (title != null) {
			FrameLayout titleFrame = new FrameLayout(context);
			// titleFrame.setBackgroundColor(context.getResources().getColor(R.color.panel_title_bg));
			titleFrame.setPadding(Views.DEFAULT_PADDING, Views.DEFAULT_PADDING, Views.DEFAULT_PADDING,
				Views.DEFAULT_PADDING);
			titleFrame.addView(title, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			ll.addView(titleFrame, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}

		ll.addView(getWrappedContent(), new LayoutParams(contentWidth, contentHeight));

		return ll;
	}

	private View getWrappedContent() {
		View innerContent = createView(content);
		// if (onClickListener != null) {
		// LinearLayout horizontal = Views.horizontal(context);
		// horizontal.addView(innerContent, Views.lp());
		// horizontal.addView(Views.text(context, ">"), Views.lpWrap());
		// innerContent = horizontal;
		// }
		if (contentPadding) {
			innerContent = Views.frame(innerContent, Views.DEFAULT_PADDING);
		}
		return innerContent;
	}

	public PanelBuilder setContent(View view) {
		this.content = view;
		return this;
	}

	public LayoutBuilder createContent() {
		LayoutBuilder lb = new LayoutBuilder(context);
		content = lb;
		return lb;
	}

	public PanelBuilder setContent(CharSequence text) {
		return setContent(Views.text(context, text));
	}

	public PanelBuilder setContent(int stringResId) {
		return setContent(Views.text(context, stringResId));
	}

	public PanelBuilder setTitle(View title) {
		this.title = title;
		return this;
	}

	public PanelBuilder setTitle(CharSequence text) {
		if (text == null) return this;
		TextView tv = Views.text(context, Str.uppercase(text));
		prepareTitleTextView(tv);
		return setTitle(tv);
	}

	private void prepareTitleTextView(TextView text) {
		Resources resources = context.getResources();
		text.setTextColor(resources.getColor(R.color.panel_title));
		text.setTypeface(null, Typeface.BOLD);
	}

	public PanelBuilder setContentPadding(boolean contentPadding) {
		this.contentPadding = contentPadding;
		return this;
	}

	public PanelBuilder setContentWidth(int contentWidth) {
		this.contentWidth = contentWidth;
		return this;
	}

	public PanelBuilder setContentHeight(int contentHeight) {
		this.contentHeight = contentHeight;
		return this;
	}

	public PanelBuilder setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
		return this;
	}

}
