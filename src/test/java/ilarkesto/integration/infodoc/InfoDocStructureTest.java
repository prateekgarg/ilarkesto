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
package ilarkesto.integration.infodoc;

import ilarkesto.testng.ATest;

import java.util.List;

import org.testng.annotations.Test;

public class InfoDocStructureTest extends ATest {

	@Test
	public void parse1() {
		InfoDocStructure doc = InfoDocStructure.parse("Absatz mit einer Zeile.\n\n" + "Absatz mit\nzwei Zeilen.\n\n"
				+ "# Kommentar\n\n" + "! Überschrift\n\n" + "@ref\n\n");
		List<AInfoDocElement> elements = doc.getElements();
		assertSize(elements, 5);

		assertEquals(elements.get(0).toString(), "Absatz mit einer Zeile.");
		assertEquals(elements.get(1).toString(), "Absatz mit\nzwei Zeilen.");
		assertEquals(elements.get(2).toString(), "Kommentar");
		assertEquals(elements.get(3).toString(), "Überschrift");
		assertEquals(elements.get(4).toString(), "ref");
	}

	@Test
	public void parse2() {
		InfoDocStructure doc = InfoDocStructure.parse("! Überschrift\n\n@ref");
		List<AInfoDocElement> elements = doc.getElements();
		assertSize(elements, 2);
	}

}
