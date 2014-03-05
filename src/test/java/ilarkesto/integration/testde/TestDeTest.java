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

import ilarkesto.base.Str;
import ilarkesto.core.base.Parser.ParseException;
import ilarkesto.core.time.Date;
import ilarkesto.integration.testde.TestDe.Article;
import ilarkesto.integration.testde.TestDe.ArticleRef;
import ilarkesto.integration.testde.TestDe.SubArticleRef;
import ilarkesto.testng.ATest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestDeTest extends ATest {

	private String username;
	private String password;

	@BeforeClass
	public void loadCredentials() throws FileNotFoundException, IOException {
		File file = new File("runtimedata/test.de.properties");
		if (!file.exists()) return;
		Properties properties = new Properties();
		properties.load(new BufferedReader(new FileReader(file)));
		username = properties.getProperty("username");
		password = properties.getProperty("password");
	}

	@Test
	public void loginBad() {
		try {
			TestDe.login(Str.generateRandomWord(5, 10, false), Str.generatePassword());
			fail("Exception expected");
		} catch (Exception ex) {}
	}

	@Test
	public void loginGood() {
		TestDe.login(username, password);
	}

	@Test
	public void removeSpamSpritpreisApps() throws ParseException {
		String html = TestDe.downloadPageHtml("Spritpreis-Apps-im-Datenschutz-Test-Vier-sind-kritisch-4663692-4663694",
			observer);
		html = TestDe.removeSpamFromPageHtml(html);
		assertContains(html,
			"Bezieht die Daten ausschließ­lich direkt von der Markt­trans­parenz­stelle für Kraft­stoffe.");
		assertContains(html, "Von den getesteten Sprit­preis-Apps wurde keine als sehr kritisch einge­stuft.");
		assertContainsNot(html, "Zurück zum Artikel");
	}

	@Test
	public void removeSpamAktienfonds() throws ParseException {
		String html = TestDe.downloadPageHtml("Aktienfonds-Welt-Fondsanlage-leicht-gemacht-4668525-0", observer);
		html = TestDe.removeSpamFromPageHtml(html);
		assertContains(html, "Noch nie war es so einfach, interna­tional in Aktien anzu­legen.");
		assertContainsNot(html, "Sie benötigen den Flash-Player");
	}

	@Test
	public void removeSpamEbook() throws ParseException {
		String html = TestDe.downloadPageHtml("E-Book-Reader-Duell-der-Nachfolger-4661050-0", observer);
		html = TestDe.removeSpamFromPageHtml(html);
		assertContains(html, "Wenn Sie den Test frei­schalten, erfahren Sie, wer am Schluss die Nase vorn hatte.");
		assertContains(html,
			"Eine Tabelle verrät zudem, welche guten Geräte aus älteren Tests immer noch erhältlich sind.");
		assertContainsNot(html, "Themenseiten");
		assertContainsNot(html, "Stiftung Warentest Abonnements");
		assertContainsNot(html, "Kompletten Artikel freischalten");
		assertContainsNot(html, "Kommentare");
		assertContainsNot(html, "Lesen Sie auf der nächsten Seite");
		assertContainsNot(html, "Liste schließen");
	}

	@Test
	public void downloadArticleAbrufkredit() throws ParseException {
		ArticleRef ref = new ArticleRef(new Date(2014, 2, 18),
				"Abruf­kredit: Diese Banken bieten güns­tige und flexible Kredite",
				"Abrufkredit-Diese-Banken-bieten-guenstige-und-flexible-Kredite-4667323-0");
		Article article = TestDe.downloadArticle(ref, observer);
		log.info(article);
		List<SubArticleRef> subArticles = article.getSubArticles();
		assertNotEmpty(subArticles);
		for (SubArticleRef sub : subArticles) {
			log.info("  ", sub);
		}

		String summary = article.getSummary();
		assertContains(summary, "Der Abruf- oder Rahmenkredit ist ein Nischen­produkt in der Banken­land­schaft.");
		assertContains(
			summary,
			" Eine Tabelle nennt unter anderem die Höhe des Kredit­rahmens, die Mindest­rück­zahlungs­raten und den Effektiven Jahres­zins des jeweiligen Angebots.");
	}

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

		String summary = article.getSummary();
		assertContains(summary, "Ob Beutels­auger oder Sauger mit Staubbox");
		assertContains(summary, "Einem Gerät reichen bereits 870 Watt für gute Saug­ergeb­nisse.");
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
		assertSize(subArticles, 5);
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
