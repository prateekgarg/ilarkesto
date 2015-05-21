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
package ilarkesto.tools.enhavo;

import ilarkesto.json.JsonObject;
import ilarkesto.templating.Context;
import ilarkesto.templating.Template;
import ilarkesto.templating.TemplateResolver;

import java.io.File;
import java.util.List;

public class FilePageContext extends APageContext implements TemplateResolver {

	private File contentFile;
	private JsonObject page;
	private JsonObject content;
	private String templatePath;
	private Template template;

	public FilePageContext(SiteContext site, File contentFile) {
		super(site);
		this.contentFile = contentFile;
	}

	@Override
	protected void onBuild() {
		page = JsonObject.loadFile(contentFile, false);

		templatePath = page.getString("template");
		template = site.getTemplate(templatePath);
		if (template == null) {
			error("ABORTED");
			return;
		}

		JsonObject multipage = page.getObject("multipage-content");
		if (multipage == null) {
			// single page
			content = page.getObject("content");
			if (content != null) processContent(content);
			String outputPath = site.getRelativePath(contentFile);
			outputPath = outputPath.replace(".json", ".html");
			processTemplate(outputPath);
		} else {
			// multiple pages
			processContent(multipage);
			List<JsonObject> contents = multipage.getArrayOfObjects("list");
			if (contents == null || contents.isEmpty()) {
				info("Empty multipage list");
				return;
			}
			int i = 0;
			for (JsonObject content : contents) {
				this.content = content;
				String outputPath = site.getRelativePath(contentFile);
				outputPath = outputPath.replace(".json", "-" + i + ".html");
				processTemplate(outputPath);
				i++;
			}
		}

	}

	private void processTemplate(String outputPath) {
		Context templateContext = createTemplateContext();
		template.process(templateContext);
		site.writeOutputFile(outputPath, templateContext.popOutput());
	}

	private Context createTemplateContext() {
		Context context = new Context();
		context.setTemplateResolver(this);
		context.setScope(content);
		context.put("content", content);
		return context;
	}

	@Override
	public Template getTemplate(String path) {
		int idx = templatePath.lastIndexOf('/');
		if (idx > 1) {
			path = templatePath.substring(0, idx) + "/" + path;
		}
		return site.getTemplate(path);
	}

	@Override
	public String toString() {
		return site.getRelativePath(contentFile);
	}

}
