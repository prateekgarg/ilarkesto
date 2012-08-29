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
package ilarkesto.ui.web.jqm;

import ilarkesto.integration.jquery.JqueryMobileDownloader;
import ilarkesto.ui.web.HtmlRenderer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class JqmHtmlPage extends AContainerElement {

	private String title;
	private String language;

	private String jqmVersion;

	private List<String> javascripts = new ArrayList<String>();
	private List<String> csss = new ArrayList<String>();

	public JqmHtmlPage(String title, String language) {
		super();
		this.title = title;
		this.language = language;
	}

	public void addJavascript(String path) {
		javascripts.add(path);
	}

	public void addCss(String path) {
		csss.add(path);
	}

	public Page addPage() {
		return addChild(new Page());
	}

	@Override
	protected void renderHeader(HtmlRenderer html) {
		html.startHTML();
		html.startHEAD(title, language);

		if (jqmVersion == null) {
			// TODO organizanto needs changes to remove this
			html.LINKcss("jqm/jquery.mobile.css");
			for (String path : csss) {
				html.LINKcss(path);
			}
			html.SCRIPTjavascript("jqm/jquery.js", null);
			for (String path : javascripts) {
				html.SCRIPTjavascript(path, null);
			}
			html.SCRIPTjavascript("jqm/jquery.mobile.js", null);
		} else {
			String jqVersion = JqueryMobileDownloader.getCompatibleJqueryVersion(jqmVersion);
			html.LINKcss("lib/jquery.mobile/jquery.mobile-" + jqmVersion + ".min.css");
			for (String path : csss) {
				html.LINKcss(path);
			}
			html.SCRIPTjavascript("lib/jquery/jquery-" + jqVersion + ".min.js", null);
			for (String path : javascripts) {
				html.SCRIPTjavascript(path, null);
			}
			html.SCRIPTjavascript("lib/jquery.mobile/jquery.mobile-" + jqmVersion + ".min.js", null);
		}

		html.endHEAD();
		html.startBODY();
	}

	@Override
	protected void renderFooter(HtmlRenderer html) {
		html.endBODY();
		html.endHTML();
	}

	public void write(PrintWriter out, String encoding) {
		HtmlRenderer html = new HtmlRenderer(out, encoding);
		render(html);
		html.flush();
	}

	public void write(File file, String encoding) {
		HtmlRenderer html;
		try {
			html = new HtmlRenderer(file, encoding);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		render(html);
		html.close();
	}

	public void setJqmVersion(String jqueryMobileVersion) {
		this.jqmVersion = jqueryMobileVersion;
	}

}
