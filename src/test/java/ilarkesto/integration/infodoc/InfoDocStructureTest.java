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
	public void bgbAnfechtungsgrund() {
		InfoDocStructure doc = InfoDocStructure
				.parse("A) @bgb-inhaltsirrtum\r\n\r\nB) @bgb-erklaerungsirrtum\r\n\r\nC) @bgb-eigenschaftsirrtum\r\n\r\nD) @bgb-uebermittlirrtum\r\n\r\nE) @bgb-123\r\n\r\nI. @bgb-arglistigetaeuschungAnfechtung\r\n\r\nII. @bgb-widerrechtlDrohung");
		List<AInfoDocElement> elements = doc.getElements();
		assertSize(elements, 7);

		assertTrue(doc.isPrefixingRequired());
	}

	@Test
	public void refWithAlternativeTitle() {
		InfoDocStructure doc = InfoDocStructure.parse("@ref/Alternative Überschrift\n\n" + "@ref2");
		List<AInfoDocElement> elements = doc.getElements();
		assertSize(elements, 2);

		assertEquals(elements.get(0).toString(), "ref");
		assertEquals(elements.get(1).toString(), "ref2");
	}

	@Test
	public void parse1() {
		InfoDocStructure doc = InfoDocStructure.parse("Absatz mit einer Zeile.\n\n" + "Absatz mit\nzwei Zeilen.\n\n"
				+ "# Kommentar\n\n" + "! Überschrift\n\n" + "@ref\n\n" + "(3) x\n\n");
		List<AInfoDocElement> elements = doc.getElements();
		assertSize(elements, 6);

		assertEquals(elements.get(0).toString(), "Absatz mit einer Zeile.");
		assertFalse(elements.get(0).isPrefixed());
		assertEquals(elements.get(0).getIndexInDepth(), 0);

		assertEquals(elements.get(1).toString(), "Absatz mit\nzwei Zeilen.");
		assertFalse(elements.get(1).isPrefixed());

		assertEquals(elements.get(2).toString(), "Kommentar");
		assertFalse(elements.get(2).isPrefixed());

		assertEquals(elements.get(3).toString(), "Überschrift");
		assertTrue(elements.get(3).isPrefixed());

		assertEquals(elements.get(4).toString(), "ref");
		assertFalse(elements.get(4).isPrefixed());

		assertEquals(elements.get(5).toString(), "x");
		assertTrue(elements.get(5).isPrefixed());
	}

	@Test
	public void parse2() {
		InfoDocStructure doc = InfoDocStructure.parse("! Überschrift\n\n@ref");
		List<AInfoDocElement> elements = doc.getElements();
		assertSize(elements, 2);
	}

	@Test
	public void parse3() {
		InfoDocStructure doc = InfoDocStructure.parse("A) 1\n\n" + "B) 2\n\n" + "I. 2.1\n\n" + "1. 2.1.1\n\n"
				+ "a) 2.1.1.1\n\n" + "aa))\n\n" + "aaa))) x\n\n");
		List<AInfoDocElement> elements = doc.getElements();
		assertEquals(elements.get(0).getDepth(), 0);
		assertEquals(elements.get(1).getDepth(), 0);
		assertEquals(elements.get(2).getDepth(), 1);
		assertEquals(elements.get(3).getDepth(), 2);
		assertEquals(elements.get(4).getDepth(), 3);
	}

	@Test
	public void parse4() {
		InfoDocStructure doc = InfoDocStructure.parse("A) 1\n\nsub");
		List<AInfoDocElement> elements = doc.getElements();

		assertEquals(elements.get(0).getDepth(), 0);

		Paragraph sub = (Paragraph) elements.get(1);
		assertEquals(sub.getDepth(), 1);
		assertEquals(sub.getText(), "sub");
	}

	@Test
	public void depth() {
		InfoDocStructure doc = InfoDocStructure.parse("! 1\n\n" + "! 2\n\n" + "!! 2.1\n\n" + "text\n\n" + "!! 2.2\n\n");
		List<AInfoDocElement> elements = doc.getElements();
		assertEquals(elements.get(0).getDepth(), 0);
		assertEquals(elements.get(1).getDepth(), 0);
		assertEquals(elements.get(2).getDepth(), 1);
		assertEquals(elements.get(3).getDepth(), 2);
		assertEquals(elements.get(4).getDepth(), 1);
	}

	@Test
	public void depth2() {
		InfoDocStructure doc = InfoDocStructure.parse("Grds. besteht Genehmigungsbedürftigkeit.\n" + "\n"
				+ "Ausnahmen bestehen bei den §§ 65, 66, 67 BauO.");
		List<AInfoDocElement> elements = doc.getElements();
		assertEquals(elements.get(0).getDepth(), 0);
		assertEquals(elements.get(1).getDepth(), 0);
	}

	@Test
	public void index() {
		InfoDocStructure doc = InfoDocStructure.parse("! 1\n\n" + "! 2\n\n" + "!! 2.1\n\n" + "text\n\n" + "text2\n\n");
		List<AInfoDocElement> elements = doc.getElements();
		assertEquals(elements.get(0).getIndexInDepth(), 0);
		assertEquals(elements.get(1).getIndexInDepth(), 1);
		assertEquals(elements.get(2).getIndexInDepth(), 0);
		assertEquals(elements.get(3).getIndexInDepth(), 0);
		assertEquals(elements.get(4).getIndexInDepth(), 0);
	}

}
