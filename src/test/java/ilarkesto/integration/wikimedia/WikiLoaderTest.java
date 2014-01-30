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
package ilarkesto.integration.wikimedia;

import ilarkesto.core.base.OperationObserver;
import ilarkesto.net.HttpDownloader;
import ilarkesto.testng.ATest;

import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

public class WikiLoaderTest extends ATest {

	@Test
	public void categoryPresidents() {
		List<PageRef> pages = getWikipediaDeLoader().loadCategoryMembers(257135, OperationObserver.DUMMY);
		assertNotEmpty(pages);
		assertTrue(pages.size() > 20);
	}

	@Test
	public void infoboxMercedes() {
		Map<String, String> info = getWikipediaDeLoader().loadInfobox(463628, OperationObserver.DUMMY);
		assertNotNull(info);
		System.out.println(info);
		assertEquals(info.get("Marke"), "Mercedes-Benz-PKW|Mercedes-Benz");
		assertEquals(info.get("Bezeichnung"), "C-Klasse");
	}

	@Test
	public void infoboxPGO() {
		Map<String, String> info = getWikipediaDeLoader().loadInfobox(6538315, OperationObserver.DUMMY);
		assertNotNull(info);
		System.out.println(info);
		assertEquals(info.get("Marke"), "PGO Automobiles|P.G.O");
		assertEquals(info.get("Modell"), "356 Classic");
		// assertEquals(info.get("Hersteller"), "P.G.O Automobiles");
	}

	@Test
	public void infoboxAbarth() {
		Map<String, String> info = getWikipediaDeLoader().loadInfobox(7637860, OperationObserver.DUMMY);
		assertNotNull(info);
		System.out.println(info);
		assertEquals(info.get("Marke"), "Simca");
		assertEquals(info.get("Modell"), "Abarth-Simca 2000");
	}

	@Test
	public void infoboxRomeo() {
		Map<String, String> info = getWikipediaDeLoader().loadInfobox(7575141, OperationObserver.DUMMY);
		assertNotNull(info);
		System.out.println(info);
		assertEquals(info.get("Marke"), "Alfa Romeo");
		assertEquals(info.get("Modell"), "4C");
	}

	@Test
	public void w203() {
		String content = getWikipediaDeLoader().loadPageContent(463628, false, false, OperationObserver.DUMMY);
		assertNotNull(content);
		System.out.println(content);
	}

	private WikiLoader getWikipediaDeLoader() {
		WikiLoader loader = new WikiLoader(new HttpDownloader(), WikiLoader.BASE_URL_WIKIPEDIA_DE);
		return loader;
	}
}
