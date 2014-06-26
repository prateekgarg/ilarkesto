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
package ilarkesto.integration.selenium;

import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SeleniumTestsuiteBuilder {

	private static final Log log = Log.get(SeleniumTestcaseBuilder.class);

	private String name;
	private List<SeleniumTestcaseBuilder> testcases = new ArrayList<SeleniumTestcaseBuilder>();

	public SeleniumTestsuiteBuilder(String name) {
		super();
		this.name = name;
	}

	public void write(File dir) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
		sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n");
		sb.append("<head>\n");
		sb.append("  <meta content=\"text/html; charset=UTF-8\" http-equiv=\"content-type\" />\n");
		sb.append("  <title>Test Suite</title>\n");
		sb.append("</head><body>\n");
		sb.append("<table id=\"suiteTable\" cellpadding=\"1\" cellspacing=\"1\" border=\"1\" class=\"selenium\"><tbody>\n");
		sb.append("  <tr><td><b>Test Suite</b></td></tr>\n");
		for (SeleniumTestcaseBuilder testcase : testcases) {
			write(dir, testcase);
			sb.append("  <tr><td><a href=\"").append(testcase.getTitle()).append("\">").append(testcase.getTitle())
					.append("</a></td></tr>\n");
		}
		sb.append("</tbody></table></body></html>\n");
		File file = new File(dir.getPath() + "/" + name);
		boolean changed = IO.writeFileIfChanged(file, sb.toString(), IO.UTF_8);
		if (changed) log.info("testsuite:", file);
	}

	private void write(File dir, SeleniumTestcaseBuilder testcase) {
		File file = new File(dir.getPath() + "/" + testcase.getTitle());
		boolean changed = IO.writeFileIfChanged(file, testcase.toString(), IO.UTF_8);
		if (changed) log.info("testcase:", file);
	}

	public void add(SeleniumTestcaseBuilder... testcases) {
		for (SeleniumTestcaseBuilder testcase : testcases) {
			this.testcases.add(testcase);
		}
	}

	public List<SeleniumTestcaseBuilder> getTestcases() {
		return testcases;
	}

}
