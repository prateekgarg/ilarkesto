package ilarkesto.android;

import ilarkesto.core.logging.Log;

import java.net.URLEncoder;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.Toast;

public class Android {

	private static Log log = Log.get(Android.class);

	public static void showToast(CharSequence text, Context context) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static boolean isInternetConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) return false;
		return networkInfo.isConnected();
	}

	public static boolean isWifiConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo == null) return false;
		return networkInfo.isConnected();
	}

	public static boolean isOnline(Context context) {
		NetworkInfo netInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		if (netInfo == null) return false;
		return netInfo.isConnectedOrConnecting();
	}

	public static void startSendEmail(Context context, String email, String subject) {
		startSendEmail(context, email, subject, "");
	}

	public static void startSendEmail(Context context, String email, String subject, String text) {
		final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("plain/text");
		intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { email });
		intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(android.content.Intent.EXTRA_TEXT, text);
		context.startActivity(intent);
	}

	public static void startCallTelephoneActivity(Context context, String phoneNumber) {
		context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber)));
	}

	public static void startViewGeoLocationActivity(Context context, String location) {
		startViewActivity(context, "geo:0,0?q=" + URLEncoder.encode(location));
	}

	public static void startViewActivity(Context context, String uri) {
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
		context.startActivity(intent);
	}

	public static DisplayMetrics getDisplayMetrics(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metrics);
		return metrics;
	}

	public static boolean isWideEnoughForDouble(Context context) {
		DisplayMetrics metrics = getDisplayMetrics(context);
		float widthInDp = metrics.widthPixels / metrics.density;
		return widthInDp > 600;
	}

	public static boolean isScreenWidthTiny(Context context) {
		DisplayMetrics metrics = getDisplayMetrics(context);
		float widthInDp = metrics.widthPixels / metrics.density;
		return widthInDp <= 320;
	}

	public static boolean isTabletDevice(Context context) {
		// Verifies if the Generalized Size of the device is XLARGE to be
		// considered a Tablet
		boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);

		// If XLarge, checks if the Generalized Density is at least MDPI
		// (160dpi)
		if (xlarge) {
			DisplayMetrics metrics = getDisplayMetrics(context);

			// MDPI=160, DEFAULT=160, DENSITY_HIGH=240, DENSITY_MEDIUM=160,
			// DENSITY_TV=213, DENSITY_XHIGH=320
			if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT
					|| metrics.densityDpi == DisplayMetrics.DENSITY_HIGH
					|| metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM
					|| metrics.densityDpi == DisplayMetrics.DENSITY_TV
					|| metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH) {

				// Yes, this is a tablet!
				return true;
			}
		}

		// No, this is not a tablet!
		return false;
	}

	public static boolean isOrientationLandscape(Context context) {
		return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}

	public static <V extends View> V addToContainer(View parent, int containerResId, V view) {
		removeFromParent(view);
		ViewGroup container = (ViewGroup) parent.findViewById(containerResId);
		container.addView(view);
		return view;
	}

	public static <V extends View> V removeFromParent(V view) {
		ViewParent parent = view.getParent();
		if (parent == null) return view;
		if (parent instanceof ViewGroup) {
			((ViewGroup) parent).removeView(view);
			return view;
		}
		log.error("Unsupported parent type:", parent.getClass());
		return view;
	}

	public static MenuItem addMenuItem(Menu menu, int order, int titleResId, boolean showAsAction,
			final Runnable callback) {
		MenuItem item = menu.add(Menu.NONE, Menu.NONE, order, titleResId);
		item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				callback.run();
				return true;
			}
		});
		if (showAsAction) item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return item;
	}

	public static void postNotification(Context context, int smallIconResId, Bitmap largeIcon,
			CharSequence contentTitle, CharSequence contentText, CharSequence tickerText, Intent notificationIntent,
			int notificationId) {
		log.info("Posting notification:", "#" + notificationId, "->", tickerText);
		PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context, notificationId,
			notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new Notification.Builder(context).setContentTitle(contentTitle)
				.setContentText(contentText).setTicker(tickerText).setSmallIcon(smallIconResId).setLargeIcon(largeIcon)
				.setContentIntent(pendingNotificationIntent).getNotification();

		notification.setLatestEventInfo(context, contentTitle, contentText, pendingNotificationIntent);

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(notificationId, notification);
	}

	public static void cancelNotification(Context context, int notificationId) {
		log.info("Canceling notification:", "#" + notificationId);
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(notificationId);
	}

	public static void startActivity(Context context, Class<? extends Activity> activity) {
		context.startActivity(new Intent(context, activity));
	}

	public static String getMarketUri(Package appId) {
		return "market://details?id=" + appId.getName();
	}

}
