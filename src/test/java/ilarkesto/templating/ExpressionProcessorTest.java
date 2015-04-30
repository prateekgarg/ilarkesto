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

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

public class ExpressionProcessorTest extends ATest {

	@Test
	public void eval() {
		Context context = new Context();

		String a = "a-value";
		context.put("a", a);

		Map<String, Object> map = new HashMap<String, Object>();
		context.put("map", map);
		Flower flower = new Flower("rose", "red");
		map.put("flower", flower);

		ExpressionProcessor exp = new ExpressionProcessor();
		assertSame(exp.eval("a", context), a);
		assertSame(exp.eval("map", context), map);
		assertSame(exp.eval("map/flower", context), flower);
		assertSame(exp.eval("map/flower/name", context), "rose");
	}

}
