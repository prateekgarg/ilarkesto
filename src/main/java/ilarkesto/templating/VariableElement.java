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

class VariableElement extends ATemplateElement {

	private String expression;
	private String defaultValue;
	private boolean escape = true;

	public VariableElement(String expression) {
		super();
		this.expression = expression;
	}

	@Override
	public void onProcess() {
		Object value = evalExpression(expression);
		if (value == null) value = defaultValue;
		if (value == null) return;

		String formatedValue = format(value);

		if (!escape) {
			print(formatedValue);
			return;
		}

		print(escape(formatedValue));
	}

	public VariableElement setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	public VariableElement setEscape(boolean escape) {
		this.escape = escape;
		return this;
	}

	public boolean isEscape() {
		return escape;
	}

	public String getExpression() {
		return expression;
	}

}