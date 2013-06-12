package ilarkesto.android;

import ilarkesto.core.logging.Log;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class ImageDownloader {

	private static Log log = Log.get(ImageDownloader.class);

	private final String imageUrl;
	private final ImageView imageView;
	private final FilesCache cache;
	private int reqWidth;
	private int reqHeight;

	public ImageDownloader(String imageUrl, ImageView imageView) {
		this(imageUrl, imageView, AApp.get().getFilesCache());
	}

	private ImageDownloader(String imageUrl, ImageView imageView, FilesCache cache) {
		this(imageUrl, imageView, cache, -1, -1);
	}

	private ImageDownloader(String imageUrl, ImageView imageView, FilesCache cache, int reqWidth, int reqHeight) {
		super();
		this.imageUrl = imageUrl;
		this.imageView = imageView;
		this.cache = cache;
		this.reqWidth = reqWidth;
		this.reqHeight = reqHeight;
		if (imageView != null) {
			synchronized (imageView) {
				if (imageUrl != null && imageUrl.equals(imageView.getTag())) return;
				imageView.setVisibility(View.GONE);
				imageView.setTag(imageUrl);
			}
		}
		Task task = new Task();
		task.execute();
	}

	private Object getSyncObject() {
		if (imageView != null) return imageView;
		return this;
	}

	class Task extends AsyncTask<String, String, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			return download();
		}

		private Bitmap download() {
			if (cache == null) return downloadDrawable();
			File file = cache.getFile(imageUrl);
			if (file == null) {
				log.info("Image needs download:", imageUrl);
				try {
					file = cache.downloadUrl(imageUrl, imageUrl);
				} catch (Exception ex) {
					log.error("Downloading image failed:", imageUrl, ex);
					return null;
				}
				if (file == null) return downloadDrawable();
			} else {
				log.debug("Image already cached:", imageUrl);
			}
			if (imageView == null) return null;
			if (reqWidth > 0 && reqHeight > 0) return Android.loadBitmap(file, reqWidth, reqHeight);
			return BitmapFactory.decodeFile(file.getPath());
		}

		private Bitmap downloadDrawable() {
			if (imageView == null) return null;
			try {
				URL url = new URL(imageUrl);
				return BitmapFactory.decodeStream(url.openStream());
			} catch (IOException ex) {
				log.error("Downloading image failed: " + imageUrl, ex);
				return null;
			}
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			synchronized (getSyncObject()) {
				log.debug("Download finished, updating image");
				if (imageView == null) return;
				if (imageView.getTag() != imageUrl) return;
				if (bitmap == null) return;
				imageView.setVisibility(View.VISIBLE);
				imageView.setImageBitmap(bitmap);
			}
		}

	}

	public static void updateImage(String imageUrl, ViewGroup container) {
		updateImage(imageUrl, container, -1, -1);
	}

	public static void updateImage(String imageUrl, ViewGroup container, int width, int height) {
		if (container == null) return;
		if (imageUrl != null) {
			ImageView view = getImageViewFromContainer(container);
			if (view == null) {
				container.removeAllViews();
				view = new ImageView(container.getContext());
				view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				container.addView(view);
			}
			new ImageDownloader(imageUrl, view, AApp.get().getFilesCache(), width, height);
			container.setVisibility(View.VISIBLE);
		} else {
			container.setVisibility(View.GONE);
		}
	}

	private static ImageView getImageViewFromContainer(ViewGroup container) {
		if (container.getChildCount() != 1) return null;
		View child = container.getChildAt(0);
		return child instanceof ImageView ? (ImageView) child : null;
	}
}
