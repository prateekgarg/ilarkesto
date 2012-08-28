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
package ilarkesto.tools.jqmhp;

import ilarkesto.base.Sys;
import ilarkesto.core.base.Str;
import ilarkesto.core.logging.Log;
import ilarkesto.integration.jquery.JqueryDownloader;
import ilarkesto.integration.jquery.JqueryMobileDownloader;
import ilarkesto.io.IO;
import ilarkesto.json.JsonObject;
import ilarkesto.runtime.AutoProxy;
import ilarkesto.ui.web.jqm.Content;
import ilarkesto.ui.web.jqm.JqmHtmlPage;
import ilarkesto.ui.web.jqm.Listview;
import ilarkesto.ui.web.jqm.Page;

import java.io.File;

public class JqmHp {

	public static void main(String[] args) {
		if (Sys.isDevelopmentMode()) AutoProxy.update();
		int ret = executeCommandLine(args);
		System.exit(ret);
	}

	private static Log log = Log.get(JqmHp.class);

	private File hpDir;
	private Config config;

	public JqmHp(File hpDir) {
		this.hpDir = hpDir;
	}

	public JqmHp(String hpDir) {
		this(new File(hpDir));
	}

	private static int executeCommandLine(String... args) {
		if (args.length < 1) return fail("Argument required: <homepage-path>");

		String hpPath = args[0];

		JqmHp jqmHp = new JqmHp(hpPath);
		jqmHp.update();

		return 0;
	}

	public void update() {
		loadConfig();
		updateLibs();
		createHtmlFile("index.html");
		recreateHtmlFile("_template.html");
		updateHtmlFiles();
	}

	private void updateHtmlFiles() {
		File[] files = hpDir.listFiles();
		if (files == null) return;
		log.info("Updating html files:");
		for (File file : files) {
			if (file.isFile() && file.getName().endsWith(".html")) {
				log.info(" ", file.getName());
				updateHtmlFile(file);
			}
		}
	}

	private void loadConfig() {
		File configFile = getHpFile("config/jqmhp.json");
		if (configFile.exists()) {
			config = Config.load(configFile);
			return;
		}

		log.info("Creating new config file:", configFile);
		config = new Config(new JsonObject());
		config.getJson().write(configFile, true);
	}

	private void recreateHtmlFile(String name) {
		IO.writeFile(getHpFile(name), "", Sys.getFileEncoding());
	}

	private void createHtmlFile(String name) {
		File file = getHpFile(name);
		if (!file.exists()) IO.touch(file);
	}

	private void updateHtmlFile(File file) {
		if (file.length() == 0) {
			log.info("    Writing content");
			writeNewHtmlFile(file);
			return;
		}
		String s = IO.readFile(file, Sys.getFileEncoding());
		String head = Str.cutFromTo(s, "<head", "</head>");
		if (Str.isBlank(head)) {
			log.info("    No <head>");
			return;
		}

		boolean modified = false;

		String usedJqmVersion = parseUsedVersion(head, "jquery.mobile");
		if (Str.isBlank(usedJqmVersion)) {
			log.info("    No JQueryMobile referenced");
		} else {
			String jqmVersion = getJqmVersion();
			if (!jqmVersion.equals(usedJqmVersion)) {
				log.info("    Updating JQueryMobile version:", usedJqmVersion, "->", jqmVersion);
				s = s.replace("jquery.mobile-" + usedJqmVersion, "jquery.mobile-" + jqmVersion);
				modified = true;
			}
		}

		String usedJqVersion = parseUsedVersion(head, "jquery");
		if (Str.isBlank(usedJqVersion)) {
			log.info("    No JQuery referenced");
		} else {
			String jqVersion = getJqVersion();
			if (!jqVersion.equals(usedJqVersion)) {
				log.info("    Updating JQuery version:", usedJqVersion, "->", jqVersion);
				s = s.replace("jquery-" + usedJqVersion, "jquery-" + jqVersion);
				modified = true;
			}
		}

		if (modified) {
			IO.writeFile(file, s, Sys.getFileEncoding());
		}
	}

	private String parseUsedVersion(String s, String library) {
		String version = Str.cutFromTo(s, library + "-", "\"");
		version = Str.removeSuffix(version, ".js");
		version = Str.removeSuffix(version, ".css");
		version = Str.removeSuffix(version, ".min");
		return version;
	}

	private void writeNewHtmlFile(File file) {
		String title = "Initial JQM page";

		JqmHtmlPage htmlPage = new JqmHtmlPage(title, "en");

		Page page = htmlPage.addPage();
		page.addHeaderWithH1(title);
		Content content = page.addContent();
		Listview listview = content.addListview();
		listview.addItem("http://kunagi.org", "Kunagi");
		listview.addItem("http://koczewski.de", "Witoslaw Koczewski");

		htmlPage.setJqmVersion(getJqmVersion());
		htmlPage.write(file, IO.UTF_8);
	}

	private void updateLibs() {
		String jqmVersion = getJqmVersion();
		File jqmLibDir = getLibDir("jquery.mobile");
		if (JqueryMobileDownloader.isInstalled(jqmVersion, jqmLibDir)) {
			log.info("JQueryMobile ist up to date");
		} else {
			IO.delete(jqmLibDir.listFiles());
			JqueryMobileDownloader.installToDir(jqmVersion, jqmLibDir);
		}

		String jqVersion = JqueryMobileDownloader.getCompatibleJqueryVersion(jqmVersion);
		File jqLibDir = getLibDir("jquery");
		if (JqueryDownloader.isInstalled(jqVersion, jqLibDir)) {
			log.info("JQuery ist up to date");
		} else {
			IO.delete(jqmLibDir.listFiles());
			JqueryDownloader.installToDir(jqVersion, jqLibDir);
		}
	}

	private String getJqmVersion() {
		String version = config.getJqm().getVersion();
		if (Str.isBlank(version)) version = JqueryMobileDownloader.getStableVersion();
		return version;
	}

	private String getJqVersion() {
		return JqueryMobileDownloader.getCompatibleJqueryVersion(getJqmVersion());
	}

	private File getLibDir(String name) {
		return getHpFile("lib/" + name);
	}

	private File getHpFile(String path) {
		return new File(hpDir.getPath() + "/" + path);
	}

	private static int fail(String message) {
		System.err.println(message);
		return 1;
	}

}
