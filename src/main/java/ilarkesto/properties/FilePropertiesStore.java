package ilarkesto.properties;

import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;

import java.io.File;
import java.util.Properties;

public class FilePropertiesStore extends APropertiesStore {

	private static final Log LOG = Log.get(FilePropertiesStore.class);
	private static final String CHARSET = IO.UTF_8;

	private String label = getClass().getSimpleName();

	@Override
	protected Properties load() {
		Properties p;
		if (file.exists()) {
			p = IO.loadProperties(file, CHARSET);
			LOG.info("Loaded properties:", file);
		} else {
			p = new Properties();
		}
		return p;
	}

	@Override
	protected void save(Properties properties) {
		IO.saveProperties(properties, label, file);
	}

	@Override
	public String toString() {
		return file.getPath();
	}

	public FilePropertiesStore setLabel(String label) {
		this.label = label;
		return this;
	}

	// --- dependencies ---

	private File file;

	public FilePropertiesStore(File file, boolean createFileIfNotExists) {
		this.file = file;
		if (createFileIfNotExists && !file.exists()) {
			IO.touch(file);
			LOG.info("Properties file created:", file.getPath());
		}
	}

	public FilePropertiesStore(String path, boolean createFileIfNotExists) {
		this(new File(path), createFileIfNotExists);
	}

}
