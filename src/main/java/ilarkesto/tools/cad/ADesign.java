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
package ilarkesto.tools.cad;

import ilarkesto.base.Reflect;
import ilarkesto.core.base.Str;
import ilarkesto.core.localization.Localizer;
import ilarkesto.core.logging.Log;
import ilarkesto.integration.svg.ASvgElement;
import ilarkesto.integration.svg.Svg;
import ilarkesto.ui.web.HtmlBuilder;

import java.lang.reflect.Field;
import java.util.List;

public abstract class ADesign {

	private final Log log = Log.get(getClass());

	private String name;
	private Svg svg;

	public ADesign(String name) {
		super();
		this.name = name;
	}

	public void buildHtml(HtmlBuilder html) {
		html.H2(name);
		List<Field> fields = Reflect.getFields(this, true, true, false);
		for (Field field : fields) {
			buildHtml(html, field);
		}

		if (svg != null) {
			html.html(svg.toString());
			svg = null;
		}
	}

	private void buildHtml(HtmlBuilder html, Field field) {
		if (field.getDeclaringClass().equals(ADesign.class)) return;
		String fieldName = field.getName();
		if (fieldName.startsWith("log")) return;
		if (fieldName.startsWith("this$")) return;

		html.startDIV();

		Object value;
		field.setAccessible(true);
		try {
			value = field.get(this);
		} catch (IllegalArgumentException | IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}

		buildHtml(html, field.getDeclaringClass().getSimpleName(), fieldName, value);

		html.endDIV();
	}

	private void buildHtml(HtmlBuilder html, String designName, String fieldName, Object value) {
		if (value == null) return;
		log.info("buildHtml()", designName + "." + fieldName);

		if (value instanceof Iterable) {
			Iterable iterable = (Iterable) value;
			int i = 0;
			for (Object o : iterable) {
				buildHtml(html, designName, fieldName + "[" + i + "]", o);
				i++;
			}
			return;
		}

		if (value instanceof Size) {
			Size size = (Size) value;
			html.SPAN("fieldLabel", size.getName() + ": ");
			html.SPAN("fieldValue", Localizer.get().format(size.getValue(), true));
			return;
		}

		if (value instanceof Artefact) {
			Artefact artefact = (Artefact) value;
			buildHtml(html, designName, fieldName, artefact.getSvgElement());
			html.startDIV();
			html.SPAN("fieldLabel", artefact.getName() + ": ");
			html.SPAN("fieldValue", Str.format(artefact.getHtml()));
			html.endDIV();
			return;
		}

		if (value instanceof ASvgElement) {
			ASvgElement svgElement = (ASvgElement) value;
			if (svg == null) svg = new Svg();
			svg.add(svgElement);
			return;
		}

		if (value instanceof ADesign) {
			ADesign design = (ADesign) value;
			design.buildHtml(html);
			return;
		}

		html.SPAN("fieldLabel", fieldName + ": ");
		html.SPAN("fieldValue", Str.format(value));
	}
}
