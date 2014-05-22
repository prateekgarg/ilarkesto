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
		String base = Sys.getWorkDir().getAbsolutePath();

		List<String> args = new ArrayList<String>();

		// port
		args.add("-port");
		args.add(String.valueOf(port));

		// workdir
		args.add("-workDir");
		args.add(base + "/runtimedata/gwt-code-server-output");

		// sources
		for (String source : sources) {
			args.add("-src");
			if (!source.startsWith("/")) source = base + "/" + source;
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

}
