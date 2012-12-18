package ilarkesto.android;

import ilarkesto.core.logging.Log;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

public class ImageDownloader {

	private static Log log = Log.get(ImageDownloader.class);

	private final String imageUrl;
	private final ImageView imageView;
	private final FilesCache cache;

	public ImageDownloader(String imageUrl, ImageView imageView, FilesCache cache) {
		super();
		this.imageUrl = imageUrl;
		this.imageView = imageView;
		this.cache = cache;
		if (imageView != null) {
			synchronized (imageView) {
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

	class Task extends AsyncTask<String, String, Drawable> {

		@Override
		protected Drawable doInBackground(String... params) {
			return download();
		}

		private Drawable download() {
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
			return imageView == null ? null : Drawable.createFromPath(file.getPath());
		}

		private Drawable downloadDrawable() {
			if (imageView == null) return null;
			try {
				URL url = new URL(imageUrl);
				return Drawable.createFromStream(url.openStream(), "src");
			} catch (IOException ex) {
				log.error("Downloading image failed: " + imageUrl, ex);
				return null;
			}
		}

		@Override
		protected void onPostExecute(Drawable drawable) {
			synchronized (getSyncObject()) {
				log.debug("Download finished, updating image");
				if (imageView == null) return;
				if (imageView.getTag() != imageUrl) return;
				if (drawable == null) return;
				imageView.setVisibility(View.VISIBLE);
				imageView.setImageDrawable(drawable);
			}
		}

	}

}
