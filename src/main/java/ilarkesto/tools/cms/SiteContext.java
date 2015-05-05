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
package ilarkesto.tools.cms;

import ilarkesto.core.base.Str;
import ilarkesto.core.parsing.ParseException;
import ilarkesto.io.IO;
import ilarkesto.templating.MustacheLikeTemplateParser;
import ilarkesto.templating.Template;
import ilarkesto.templating.TemplateResolver;

import java.io.File;

public class SiteContext extends ABuilder implements TemplateResolver {

	private File dir;
	private File contentDir;
	private File templatesDir;

	private File outputDir;

	public SiteContext(CmsContext cms, File dir) {
		super(cms);
		this.dir = dir;

		contentDir = new File(dir.getPath() + "/content");
		templatesDir = new File(dir.getPath() + "/templates");
	}

	@Override
	protected void onBuild() {
		outputDir = new File(cms.getOutputDir().getPath() + "/" + dir.getName());

		processContentFiles(contentDir);
	}

	private void processContentFiles(File dir) {
		for (File file : IO.listFiles(dir)) {
			if (file.isDirectory()) {
				processContentFiles(file);
				continue;
			}
			String name = file.getName();
			if (name.endsWith(".page.json")) {
				ContentFilePageContext page = new ContentFilePageContext(this, file);
				page.build();
				continue;
			}
			writeOutputFile(getRelativePath(file), file);
		}
	}

	public void writeOutputFile(String path, String text) {
		File file = getOutputFile(path);
		info("output>", file.getPath());
		IO.writeFile(file, text, IO.UTF_8);
	}

	public void writeOutputFile(String path, File source) {
		File file = getOutputFile(path);
		info("output>", file.getPath());
		IO.copyFile(source, file);
	}

	private File getOutputFile(String path) {
		return new File(outputDir.getPath() + "/" + path);
	}

	public File getDir() {
		return dir;
	}

	public File getContentDir() {
		return contentDir;
	}

	@Override
	public String toString() {
		return dir.getName();
	}

	@Override
	public Template getTemplate(String templatePath) {
		File file = findTemplateFile(templatePath);
		if (file == null) {
			error("Template not found:", templatePath);
			return null;
		}
		info("template:", templatePath, "->", file.getPath());
		try {
			return MustacheLikeTemplateParser.parseTemplate(file);
		} catch (ParseException ex) {
			throw new RuntimeException(ex);
		}
	}

	public File findTemplateFile(String templatePath) {
		File file = new File(templatesDir.getPath() + "/" + templatePath);
		if (file.exists()) return file;
		return cms.findTemplateFile(templatePath);
	}

	public String getRelativePath(File file) {
		return Str.removePrefix(file.getPath(), getContentDir().getPath() + "/");
	}

}
