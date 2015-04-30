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

import ilarkesto.core.parsing.ParseException;
import ilarkesto.testng.ATest;

import java.util.Arrays;

import org.testng.annotations.Test;

public class MustacheLikeTemplateParserTest extends ATest {

	@Test
	public void templating() throws ParseException {
		assertTemplateOutput("hello world", "hello world");
		assertTemplateOutput("hello {{nonExistingVariable}}", "hello ");
		assertTemplateOutput("hello {{a}}", "hello a-value");
		assertTemplateOutput("hello {{#rose}}existing{{/}}", "hello existing");
		assertTemplateOutput("hello {{#rose}}{{color}}{{/}}", "hello red");
	}

	@Test
	public void loopWithContent() throws ParseException {
		Template template = MustacheLikeTemplateParser.parseTemplate("{{#flower}}hello world{{/}}");
		assertNotEmpty(template.children);

		LoopElement loop = (LoopElement) template.children.get(0);
		assertEquals(loop.getExpression(), "flower");

		assertNotEmpty(loop.children);
		TextElement text = (TextElement) loop.children.get(0);
		assertEquals(text.getText(), "hello world");
	}

	@Test
	public void loopEmpty() throws ParseException {
		Template template = MustacheLikeTemplateParser.parseTemplate("{{#flower}}{{/}}");
		assertNotEmpty(template.children);
		LoopElement loop = (LoopElement) template.children.iterator().next();
		assertEquals(loop.getExpression(), "flower");
	}

	@Test
	public void include() throws ParseException {
		Template template = MustacheLikeTemplateParser.parseTemplate("{{> incl}}");
		assertNotEmpty(template.children);
		IncludeElement variable = (IncludeElement) template.children.get(0);
		assertEquals(variable.getPath(), "incl");
	}

	@Test
	public void variable() throws ParseException {
		Template template = MustacheLikeTemplateParser.parseTemplate("{{a}}");
		assertNotEmpty(template.children);
		VariableElement variable = (VariableElement) template.children.get(0);
		assertEquals(variable.getExpression(), "a");
	}

	@Test
	public void text() throws ParseException {
		Template template = MustacheLikeTemplateParser.parseTemplate("hello world");
		assertNotEmpty(template.children);
		TextElement text = (TextElement) template.children.get(0);
		assertEquals(text.getText(), "hello world");
	}

	private static void assertTemplateOutput(String templateCode, String output) throws ParseException {
		Template template = MustacheLikeTemplateParser.parseTemplate(templateCode);
		Context context = new Context();
		context.put("a", "a-value");
		context.put("rose", new Flower("rose", "red"));
		context.put("flowers",
			Arrays.asList(new Flower("rose", "white"), new Flower("rose", "red"), new Flower("tulip", "yellow")));
		template.process(context);
		assertEquals(context.popOutput(), output);
	}
}
