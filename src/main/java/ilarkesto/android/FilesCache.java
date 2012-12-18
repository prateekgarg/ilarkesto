package ilarkesto.android;

import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class FilesCache {

	private final Log log = Log.get(FilesCache.class);

	private final Context context;
	private final String id;

	public FilesCache(Context context, String id) {
		super();
		this.context = context;
		this.id = id;
	}

	public File getFile(String key) {
		if (key == null) return null;
		File file = getFilename(key);
		return file.exists() ? file : null;
	}

	public Bitmap getBitmap(String key) {
		File file = getFile(key);
		if (file == null) return null;
		return BitmapFactory.decodeFile(file.getPath());
	}

	public File downloadUrl(String url) {
		return downloadUrl(url, url);
	}

	public File downloadUrl(String key, String url) {
		File file = getFilename(key);
		File dir = file.getParentFile();
		if (!dir.exists() && !dir.mkdirs()) {
			log.info("Saving file failed. Cache dir can not be created:", dir);
			return null;
		}
		IO.downloadUrlToFile(url, file.getPath());
		return file;
	}

	private File getFilename(String key) {
		return new File(context.getExternalCacheDir().getPath() + "/FileCache." + toFilename(id) + "/"
				+ toFilename(key));
	}

	private String toFilename(String key) {
		return key.replaceAll("[.:/,%?&=]", "+").replaceAll("[+]+", "+");
	}

}
