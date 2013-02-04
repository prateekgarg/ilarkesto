package ilarkesto.android;

import ilarkesto.core.logging.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

public class Swipe {

	private static final Log log = Log.get(Swipe.class);

	public static final int SWIPE_NONE = 0;
	public static final int SWIPE_LEFT = 1;
	public static final int SWIPE_RIGHT = 2;

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	public static void attachOnSwipeListener(View view, final OnSwipeListener listener) {
		final GestureDetector gestureDetector = new GestureDetector(new SimpleOnGestureListener() {

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				try {
					if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) return false;
					if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
						listener.onSwipeRight();
					} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
							&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
						listener.onSwipeLeft();
					}
				} catch (Exception e) {
					log.error(e);
				}
				return false;

			}

		});
		OnTouchListener gestureListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};
		view.setOnTouchListener(gestureListener);
	}

	public static void animate(int swipeMode, final ViewGroup container, final View oldView, View newView) {
		if (swipeMode == SWIPE_NONE) {
			container.removeView(oldView);
			container.addView(newView);
			return;
		}

		int duration = 250;

		int width = swipeMode == SWIPE_RIGHT ? container.getWidth() : -container.getWidth();
		TranslateAnimation hide = new TranslateAnimation(0, -width, 0, 0);
		hide.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				oldView.setVisibility(View.GONE);
				container.removeView(oldView);
			}
		});
		hide.setDuration(duration);
		oldView.startAnimation(hide);

		TranslateAnimation show = new TranslateAnimation(width, 0, 0, 0);
		show.setFillAfter(true);
		show.setDuration(duration);

		container.addView(newView);
		newView.startAnimation(show);
	}

	public static interface OnSwipeListener {

		void onSwipeLeft();

		void onSwipeRight();

	}

}
