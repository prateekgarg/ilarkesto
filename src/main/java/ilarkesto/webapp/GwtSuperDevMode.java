/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package ilarkesto.webapp;

import ilarkesto.base.Sys;
import ilarkesto.core.base.RuntimeTracker;
import ilarkesto.core.base.Str;
import ilarkesto.core.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.codeserver.CodeServer;
import com.google.gwt.dev.codeserver.Options;
import com.google.gwt.dev.codeserver.WebServer;

public class GwtSuperDevMode {

	private static Log log = Log.get(GwtSuperDevMode.class);

	private int port = 9876;
	private Set<String> sources = new LinkedHashSet<String>();
	private Set<String> modules = new LinkedHashSet<String>();
	private WebServer webServer;

	public void startCodeServer() {
		if (webServer != null) throw new IllegalStateException("Already started");
		log.info("Starting GWT Super Dev Mode CodeServer on port", port);
		Sys.setProperty("gwt.codeserver.port", String.valueOf(port));
		RuntimeTracker rt = new RuntimeTracker();
		Options options = createOptions();
		try {
			webServer = CodeServer.start(options);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} catch (UnableToCompleteException ex) {
			throw new RuntimeException(ex);
		}
		log.info("CodeServer started in", rt.getRuntimeFormated());
	}

	public void stopCodeServer() {
		if (webServer != null) {
			try {
				webServer.stop();
			} catch (Exception ex) {
				log.error("Stopping CodeServer failed.", ex);
			}
		}
	}

	private Options createOptions() {
		String base = Sys.getWorkDir().getAbsolutePath().replace('\n', '/');
		if (!new File(base).exists()) throw new IllegalStateException("Path does not exist: " + base);

		List<String> args = new ArrayList<String>();

		// port
		args.add("-port");
		args.add(String.valueOf(port));

		// workdir
		args.add("-workDir");
		String workDir = base + "/runtimedata/gwt-code-server-output";
		File workdirFile = new File(workDir);
		workdirFile.mkdirs();
		if (!workdirFile.exists()) throw new IllegalStateException("Path does not exist: " + workDir);
		args.add(workDir);

		// sources
		for (String source : sources) {
			args.add("-src");
			if (!source.startsWith("/")) source = base + "/" + source;
			File sourceFile = new File(source);
			if (!sourceFile.exists()) throw new IllegalStateException("Path does not exist: " + source);
			args.add(source);
		}

		// modules
		for (String module : modules) {
			args.add(module);
		}

		Options options = new Options();
		boolean parsed = options.parseArgs(args.toArray(new String[args.size()]));
		if (!parsed) throw new RuntimeException("Parsing args failed: " + Str.format(args));
		return options;
	}

	public GwtSuperDevMode addModules(String... modules) {
		for (String module : modules) {
			this.modules.add(module);
		}
		return this;
	}

	public GwtSuperDevMode addSources(String... sources) {
		for (String source : sources) {
			this.sources.add(source);
		}
		return this;
	}

	public static String getCompileHref(String moduleName) {
		return "javascript:%7B window.__gwt_bookmarklet_params %3D %7Bserver_url%3A'http%3A%2F%2Flocalhost%3A9876%2F'%2Cmodule_name%3A'"
				+ moduleName
				+ "'%7D%3B var s %3D document.createElement('script')%3B s.src %3D 'http%3A%2F%2Flocalhost%3A9876%2Fdev_mode_on.js'%3B void(document.getElementsByTagName('head')%5B0%5D.appendChild(s))%3B%7D";
	}

}
