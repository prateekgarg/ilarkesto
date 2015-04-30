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

import ilarkesto.core.parsing.ASaxParserState;
import ilarkesto.core.parsing.ParseException;

import java.io.File;

public abstract class ATemplateParser extends ASaxParserState {

	protected TemplateBuilder builder = new TemplateBuilder();

	public final Template getTemplate() {
		return builder.getTemplate();
	}

	@Override
	public ATemplateParser parse(String text) throws ParseException {
		super.parse(text);
		return this;
	}

	@Override
	public ATemplateParser parse(File file) throws ParseException {
		super.parse(file);
		return this;
	}

}
