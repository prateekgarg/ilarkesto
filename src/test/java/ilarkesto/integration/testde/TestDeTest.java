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
package ilarkesto.integration.testde;

import ilarkesto.core.base.Parser.ParseException;
import ilarkesto.core.time.Date;
import ilarkesto.integration.testde.TestDe.Article;
import ilarkesto.integration.testde.TestDe.ArticleRef;
import ilarkesto.integration.testde.TestDe.SubArticleRef;
import ilarkesto.testng.ATest;

import java.util.List;

import org.testng.annotations.Test;

public class TestDeTest extends ATest {

	@Test
	public void downloadArticleStaubsauger() throws ParseException {
		ArticleRef ref = new ArticleRef(new Date(2014, 02, 7), "Staubsauger: 74 Boden­staub­sauger im Test",
				"Staubsauger-im-Test-1838262-0");
		Article article = TestDe.downloadArticle(ref, observer);
		log.info(article);
		List<SubArticleRef> subArticles = article.getSubArticles();
		assertNotEmpty(subArticles);
		for (SubArticleRef sub : subArticles) {
			log.info("  ", sub);
		}
	}

	@Test
	public void downloadArticleTagesgeld() throws ParseException {
		ArticleRef ref = new ArticleRef(new Date(2014, 01, 10), "Tages­geld: Die besten Zinsen",
				"Tagesgeld-Die-besten-Zinsen-4196794-0");
		Article article = TestDe.downloadArticle(ref, observer);
		log.info(article);
		List<SubArticleRef> subArticles = article.getSubArticles();
		for (SubArticleRef sub : subArticles) {
			log.info("  ", sub);
		}
		assertSize(subArticles, 7);
	}

	@Test
	public void downloadArticleRefs() throws ParseException {
		List<ArticleRef> articles = TestDe.downloadArticleRefs(1, observer);
		assertSize(articles, 10);
		for (int i = 0; i < 10; i++) {
			log.debug(articles.get(i));
		}
	}

}
