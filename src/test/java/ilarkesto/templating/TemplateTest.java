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

import ilarkesto.testng.ATest;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TemplateTest extends ATest {

	private Context context;
	private Template template;

	@BeforeMethod
	public void init() {
		context = new Context();
		template = new Template();
	}

	@Test
	public void componentInclude() {
		final Template component = new Template();
		component.add(new IncludeElement("head"));
		component.add(new TextElement("|"));
		component.add(new IncludeElement("body"));
		context.setTemplateResolver(new TemplateResolver() {

			@Override
			public Template getTemplate(String path) {
				return path.equals("component") ? component : null;
			}
		});

		ComponentIncludeElement componentInclude = new ComponentIncludeElement("component");
		Template head = new Template().add(new TextElement("head-content"));
		Template body = new Template().add(new TextElement("body-content"));
		componentInclude.addTemplate("head", head);
		componentInclude.addTemplate("body", body);

		template = new Template().add(componentInclude);
		assertTemplateProcess("head-content|body-content");
	}

	@Test
	public void include() {
		final Template subtemplate = new Template().add(new TextElement("include-content"));
		context.setTemplateResolver(new TemplateResolver() {

			@Override
			public Template getTemplate(String path) {
				return path.equals("sub") ? subtemplate : null;
			}
		});

		template = new Template().add(new ComponentIncludeElement("sub"));
		assertTemplateProcess("include-content");
	}

	@Test
	public void loopObject() {
		context.put("flower", new Flower("rose", "white"));
		template = new Template().add(new LoopElement("flower", new VariableElement("color")));
		assertTemplateProcess("white");

		context.put("true", true);
		template = new Template().add(new LoopElement("true", new VariableElement("flower/color")));
		assertTemplateProcess("white");
	}

	@Test
	public void loopItems() {
		context.put("items",
			Arrays.asList(new Flower("rose", "red"), new Flower("rose", "white"), new Flower("tulip", "yellow")));

		template = new Template().add(new LoopElement("items", new VariableElement("color")));
		assertTemplateProcess("redwhiteyellow");

		template = new Template().add(new LoopElement("items", new VariableElement("$loop/index")));
		assertTemplateProcess("012");

		template = new Template().add(new LoopElement("items", new VariableElement("$loop/position")));
		assertTemplateProcess("123");

		template = new Template().add(new LoopElement("items", new VariableElement("$loop/first")));
		assertTemplateProcess("truefalsefalse");

		template = new Template().add(new LoopElement("items", new VariableElement("$loop/last")));
		assertTemplateProcess("falsefalsetrue");
	}

	@Test
	public void optional() {
		context.put("a", "a-value");
		context.put("b", "No");
		context.put("c", "false");
		context.put("d", false);
		context.put("e", Collections.emptyList());

		template = new Template().add(new OptionalElement("non-existing-child", new TextElement("non-empty")));
		assertTemplateProcess("");

		template = new Template().add(new OptionalElement("a", new TextElement("non-empty")));
		assertTemplateProcess("non-empty");

		template = new Template().add(new OptionalElement("b", new TextElement("non-empty")));
		assertTemplateProcess("");

		template = new Template().add(new OptionalElement("c", new TextElement("non-empty")));
		assertTemplateProcess("");

		template = new Template().add(new OptionalElement("d", new TextElement("non-empty")));
		assertTemplateProcess("");

		template = new Template().add(new OptionalElement("e", new TextElement("non-empty")));
		assertTemplateProcess("");
	}

	@Test
	public void scope() {
		Map<String, String> child = new HashMap<String, String>();
		child.put("name", "Duke");
		context.put("child", child);
		context.put("a", "a-value");

		template = new Template().add(new ScopeElement("non-existing-child", new TextElement("non-empty")));
		assertTemplateProcess("");

		template = new Template().add(new VariableElement("name"));
		assertTemplateProcess("");

		template = new Template().add(new ScopeElement("child", new VariableElement("name")));
		assertTemplateProcess("Duke");

		template = new Template().add(new ScopeElement("child", new VariableElement("a")));
		assertTemplateProcess("");

		template = new Template().add(new ScopeElement("child", new VariableElement("/a")));
		assertTemplateProcess("a-value");
	}

	@Test
	public void escaping() {
		context.put("html", "<html>");

		template = new Template().add(new VariableElement("html"));
		assertTemplateProcess("&lt;html&gt;");

		template = new Template().add(new VariableElement("html").setEscape(false));
		assertTemplateProcess("<html>");
	}

	@Test
	public void expressions() {
		context.put("a1", "a1-value");

		template = new Template().add(new VariableElement("a1"));
		assertTemplateProcess("a1-value");

		template = new Template().add(new VariableElement("doesNotExist"));
		assertTemplateProcess("");

		template = new Template().add(new VariableElement("doesNotExist").setDefaultValue("default"));
		assertTemplateProcess("default");

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("b1", "b1-value");
		context.setScope(data);

		template = new Template().add(new VariableElement("a1"));
		assertTemplateProcess("");

		template = new Template().add(new VariableElement("/a1"));
		assertTemplateProcess("a1-value");

		template = new Template().add(new VariableElement("b1"));
		assertTemplateProcess("b1-value");
	}

	@Test
	public void expressionFromMap() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("a", "aValue");
		context.setScope(data);

		template.add(new VariableElement("a"));
		assertTemplateProcess("aValue");

		template.add(new VariableElement("doesNotExist"));
		assertTemplateProcess("aValue");

		template.add(new VariableElement("doesNotExist").setDefaultValue("Default"));
		assertTemplateProcess("aValueDefault");
	}

	@Test
	public void text() {
		template.add(new TextElement("hello world"));
		template.add(new TextElement("\n :-D"));
		assertTemplateProcess("hello world\n :-D");
	}

	private void assertTemplateProcess(String expectedValue) {
		assertEquals(template.process(context).popOutput(), expectedValue);
	}

}
