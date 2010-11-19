package ilarkesto.integration.mswindows;

import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;

import java.io.File;

public class DllInstaller {

	private static final Log LOG = Log.get(DllInstaller.class);

	private DllInstaller() {}

	public static void installDll(String dllName, boolean deleteOnExit) {
		File file = new File(dllName + ".dll").getAbsoluteFile();
		IO.copyResource("dll/" + dllName + ".dll", file.getPath());
		LOG.info(dllName, "installed to", file.getPath());
		if (deleteOnExit) file.deleteOnExit();
	}

}
