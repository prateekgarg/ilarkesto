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
		updateIndexHtml();
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

	private void updateIndexHtml() {
		updateJqmHtmlFile(getHpFile("index.html"));
	}

	private void updateJqmHtmlFile(File file) {
		if (!file.exists()) {
			log.info("Creating new JQM file:", file);
			createJqmHtmlFile(file);
			return;
		}
	}

	private void createJqmHtmlFile(File file) {
		String title = "Initial JQM page";

		JqmHtmlPage htmlPage = new JqmHtmlPage(title, "en");

		Page page = htmlPage.addPage();
		page.addHeaderWithH1(title);
		Content content = page.addContent();
		Listview listview = content.addListview();
		listview.addItem("http://kunagi.org", "Kunagi");
		listview.addItem("http://koczewski.de", "Witoslaw Koczewski");

		htmlPage.setJqmVersion(config.getJqm().getVersion());
		htmlPage.write(file, IO.UTF_8);
	}

	private void updateLibs() {
		String jqmVersion = config.getJqm().getVersion();
		if (jqmVersion == null) jqmVersion = JqueryMobileDownloader.getStableVersion();
		JqueryMobileDownloader.installToDir(jqmVersion, getLibDir("jquery.mobile"));
		JqueryDownloader.installToDir(JqueryMobileDownloader.getCompatibleJqueryVersion(jqmVersion),
			getLibDir("jquery"));
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
