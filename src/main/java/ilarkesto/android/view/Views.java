package ilarkesto.android.view;

import ilarkesto.android.AApp;
import ilarkesto.android.Android;
import ilarkesto.android.ImageDownloader;
import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class Views {

	public static final int DEFAULT_PADDING = 20;

	public static void updateTextView(View view, int textViewResId, CharSequence text) {
		TextView textView = (TextView) view.findViewById(textViewResId);
		if (textView == null) return;
		textView.setText(text);
		if (text == null) {
			textView.setVisibility(View.GONE);
		} else {
			textView.setVisibility(View.VISIBLE);
		}
	}

	public static void hide(View parent, int id) {
		parent.findViewById(id).setVisibility(View.GONE);
	}

	public static void show(View parent, int id) {
		parent.findViewById(id).setVisibility(View.VISIBLE);
	}

	public static boolean isCoordinatesInView(View view, int rx, int ry) {
		int[] l = new int[2];
		view.getLocationOnScreen(l);
		int x = l[0];
		int y = l[1];
		int w = view.getWidth();
		int h = view.getHeight();

		if (rx < x || rx > x + w || ry < y || ry > y + h) return false;
		return true;
	}

	public static Button button(Context context, CharSequence text, Class<? extends Activity> activityToStart) {
		return button(context, text, startActivityOnClickListener(context, activityToStart));
	}

	public static void updateTextViewWithHtml(View view, int textViewResId, String html) {
		updateTextView(view, textViewResId, html == null ? null : Html.fromHtml(html));
	}

	public static View.OnClickListener startActivityOnClickListener(final Context context,
			final Class<? extends Activity> activityToStart) {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				Android.startActivity(context, activityToStart);
			}
		};
	}

	public static ImageView remoteImage(Context context, String imageUrl) {
		if (imageUrl == null) return null;
		ImageView image = new ImageView(context);
		image.setScaleType(ScaleType.FIT_CENTER);
		new ImageDownloader(imageUrl, image, AApp.get(context).getFilesCache());
		return image;
	}

	public static Button button(Context context, CharSequence text, OnClickListener onClickListener) {
		Button button = new Button(context);
		button.setText(text);
		button.setOnClickListener(onClickListener);
		return button;
	}

	public static View html(Context context, String html) {
		if (html == null) return null;
		return text(context, Html.fromHtml(html));
	}

	public static LinearLayout addSpacer(LinearLayout ll, int size) {
		if (size > 0) {
			int orientation = ll.getOrientation();
			int width = orientation == LinearLayout.VERTICAL ? 1 : size;
			int height = orientation == LinearLayout.HORIZONTAL ? 1 : size;
			ll.addView(new FrameLayout(ll.getContext()), new LayoutParams(width, height));
		}
		return ll;
	}

	public static FrameLayout frame(View content, int padding) {
		return frame(content, padding, padding);
	}

	public static FrameLayout frame(View content, int leftRight, int topBottom) {
		return frame(content, leftRight, topBottom, leftRight, topBottom);
	}

	public static FrameLayout frame(View content, int left, int top, int right, int bottom) {
		FrameLayout frame = new FrameLayout(content.getContext());
		frame.setPadding(left, top, right, bottom);
		frame.addView(content, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		return frame;
	}

	public static LayoutParams lp() {
		return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	}

	public static LayoutParams lpWrap() {
		return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	public static LinearLayout vertical(Context context, View... views) {
		// if (views == null || views.length == 0) return null;
		return linearLayout(context, LinearLayout.VERTICAL, views);
	}

	public static LinearLayout horizontal(Context context, View... views) {
		return linearLayout(context, LinearLayout.HORIZONTAL, views);
	}

	private static LinearLayout linearLayout(Context context, int orientation, View... views) {
		LinearLayout ll = new LinearLayout(context);
		ll.setOrientation(orientation);
		for (View view : views) {
			ll.addView(view);
		}
		return ll;
	}

	public static TextView text(Context context, Object text) {
		if (text == null) return null;
		TextView tv = new TextView(context);
		tv.setText(text instanceof CharSequence ? (CharSequence) text : text.toString());
		return tv;
	}

	public static TextView text(Context context, int stringResId) {
		TextView tv = new TextView(context);
		tv.setText(stringResId);
		return tv;
	}

	public static ScrollView scroller(View content) {
		ScrollView scroller = new ScrollView(content.getContext());
		scroller.addView(content, lp());
		return scroller;
	}

}
