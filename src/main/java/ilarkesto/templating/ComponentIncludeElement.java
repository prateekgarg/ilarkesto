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
package ilarkesto.templating;

import java.util.HashMap;
import java.util.Map;

public class ComponentIncludeElement extends ContainerElement {

	private String path;
	private Map<String, Template> inlineTemplates = new HashMap<String, Template>();

	public ComponentIncludeElement(String path) {
		this.path = path;
	}

	public void addTemplate(String name, Template template) {
		inlineTemplates.put(name, template);
	}

	@Override
	public void onProcess() {
		TemplateResolver previousTemplateResolver = context.getTemplateResolver();
		InlineTemplateResolver inlineTemplateResolver = new InlineTemplateResolver(previousTemplateResolver);
		context.setTemplateResolver(inlineTemplateResolver);

		Template template = getTemplate(path);
		if (template == null) return;
		template.process(context);

		super.onProcess();
		context.setTemplateResolver(previousTemplateResolver);
	}

	class InlineTemplateResolver implements TemplateResolver {

		private TemplateResolver parent;

		public InlineTemplateResolver(TemplateResolver parent) {
			super();
			this.parent = parent;
		}

		@Override
		public Template getTemplate(String path) {
			Template template = inlineTemplates.get(path);
			if (template != null) return template;

			if (parent == null) return null;
			return parent.getTemplate(path);
		}

	}

}
