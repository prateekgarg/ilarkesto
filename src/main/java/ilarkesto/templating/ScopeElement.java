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

class ScopeElement implements TemplateElement {

	private String expression;
	private TemplateElement contentTemplate;

	public ScopeElement(String expression, TemplateElement contentTemplate) {
		super();
		this.expression = expression;
		this.contentTemplate = contentTemplate;
	}

	@Override
	public Context process(Context context) {

		Object value = context.getExpressionProcessor().eval(expression, context);
		if (value == null) return context;

		Object scope = context.getScope();
		context.setScope(value);
		contentTemplate.process(context);
		context.setScope(scope);

		return context;
	}

}