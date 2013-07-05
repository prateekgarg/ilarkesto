package ilarkesto.android.view;

import ilarkesto.android.Android;
import ilarkesto.android.ImageDownloader;
import ilarkesto.android.R;
import ilarkesto.io.IO;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

public class Views {

	public static final int DEFAULT_PADDING = 20;

	public static <T> Spinner spinner(Context context, List<T> items) {
		Spinner spinner = new Spinner(context);
		spinner.setAdapter(new ArrayAdapter<T>(context, R.layout.adapteritem, items));
		return spinner;
	}

	public static View title(Context context, CharSequence text) {
		return titleWithRemoteImage(context, text, null);
	}

	public static View titleWithRemoteImage(Context context, CharSequence text, String imageUrl) {
		LinearLayout titleWrapper = titleWrapper(context);

		ImageView image = remoteImage(context, imageUrl);
		if (image != null) {
			image.setPadding(0, 5, DEFAULT_PADDING, 0);
			titleWrapper.addView(image, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}

		TextView tv = titleText(context, text);
		titleWrapper.addView(tv, lp());

		return titleWrapper;
	}

	public static TextView titleText(Context context, CharSequence text) {
		TextView tv = text(context, text);
		tv.setTextSize(20);
		// tv.setTypeface(null, Typeface.BOLD);
		tv.setTextColor(context.getResources().getColor(R.color.title));
		return tv;
	}

	public static LinearLayout titleWrapper(Context context) {
		return titleWrapper(context, DEFAULT_PADDING, DEFAULT_PADDING);
	}

	public static LinearLayout titleWrapper(Context context, int paddingLeftRight, int paddingTopBottom) {
		LinearLayout ll = horizontal(context);
		ll.setBackgroundColor(context.getResources().getColor(R.color.title_bg));
		ll.setPadding(paddingLeftRight, paddingTopBottom, paddingLeftRight, paddingTopBottom);
		return ll;
	}

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
		new ImageDownloader(imageUrl, image);
		return image;
	}

	public static ImageButton remoteImageButton(Context context, String imageUrl) {
		if (imageUrl == null) return null;
		ImageButton image = new ImageButton(context);
		image.setScaleType(ScaleType.FIT_CENTER);
		new ImageDownloader(imageUrl, image);
		return image;
	}

	public static Button button(Context context, CharSequence text, OnClickListener onClickListener) {
		Button button = new Button(context);
		button.setText(text);
		button.setOnClickListener(onClickListener);
		return button;
	}

	public static WebView html(Context context, String html) {
		return html(context, html, (String) null);
	}

	public static WebView html(Context context, String html, File baseUrlDir) {
		return html(context, html, baseUrlDir == null ? null : "file://" + baseUrlDir.getAbsolutePath() + "/");
	}

	public static WebView html(Context context, String html, String baseUrl) {
		if (html == null) return null;
		// return text(context, Html.fromHtml(html));

		WebView view = new WebView(context);
		if (baseUrl == null) {
			view.loadData(html, "text/html; charset=" + IO.UTF_8, IO.UTF_8);
		} else {
			view.loadDataWithBaseURL(baseUrl, html, "text/html", IO.UTF_8, baseUrl);
		}
		return view;
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

	public static LinearLayout verticalSpacer(Context context, int height) {
		LinearLayout spacer = vertical(context);
		addSpacer(spacer, height);
		return spacer;
	}

	public static LinearLayout horizontalSpacer(Context context, int width) {
		LinearLayout spacer = horizontal(context);
		addSpacer(spacer, width);
		return spacer;
	}

	public static FrameLayout frameMatch(View content, int padding) {
		return frameMatch(content, padding, padding);
	}

	public static FrameLayout frameMatch(View content, int leftRight, int topBottom) {
		return frameMatch(content, leftRight, topBottom, leftRight, topBottom);
	}

	public static FrameLayout frameMatch(View content, int left, int top, int right, int bottom) {
		return frame(content, left, top, right, bottom, lpMatch());
	}

	public static FrameLayout frameWrap(View content, int padding) {
		return frameWrap(content, padding, padding);
	}

	public static FrameLayout frameWrap(View content, int leftRight, int topBottom) {
		return frameWrap(content, leftRight, topBottom, leftRight, topBottom);
	}

	public static FrameLayout frameWrap(View content, int left, int top, int right, int bottom) {
		return frame(content, left, top, right, bottom, lpWrap());
	}

	public static FrameLayout frame(View content, int left, int top, int right, int bottom, LayoutParams layoutParams) {
		FrameLayout frame = new FrameLayout(content.getContext());
		frame.setPadding(left, top, right, bottom);
		frame.addView(content, layoutParams);
		return frame;
	}

	public static LayoutParams lp() {
		return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	}

	public static LayoutParams lpWrap() {
		return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	public static LayoutParams lp(int width, int height, int gravity, int weight) {
		LayoutParams lp = new LayoutParams(width, height);
		lp.gravity = gravity;
		lp.weight = weight;
		return lp;
	}

	public static LayoutParams lpWrap(int gravity, int weight) {
		LayoutParams lp = lpWrap();
		lp.gravity = gravity;
		lp.weight = weight;
		return lp;
	}

	public static LayoutParams lpWrapRight() {
		return lpWrap(Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0);
	}

	public static LayoutParams lpWrapLeft() {
		return lpWrap(Gravity.LEFT | Gravity.CENTER_VERTICAL, 0);
	}

	public static LayoutParams lpMatch() {
		return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
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

	public static HorizontalScrollView horizontalScroller(View content) {
		HorizontalScrollView scroller = new HorizontalScrollView(content.getContext());
		scroller.setScrollbarFadingEnabled(false);
		scroller.addView(content, lpWrap());
		return scroller;
	}

}
