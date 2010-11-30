package ilarkesto.net;

import ilarkesto.testng.ATest;

import java.io.File;

import org.testng.annotations.Test;

public class WebCrawlerTest extends ATest {

	@Test
	public void getBaseUrl() {
		assertEquals("http://koczewski.de/", WebCrawler.getBaseUrl("http://koczewski.de"));
		assertEquals("http://koczewski.de/", WebCrawler.getBaseUrl("http://koczewski.de/"));
		assertEquals("http://koczewski.de/", WebCrawler.getBaseUrl("http://koczewski.de/index.html"));
		assertEquals("http://koczewski.de/", WebCrawler.getBaseUrl("http://koczewski.de/start"));
	}

	@Test
	public void isProbablyHtml() {
		assertTrue(WebCrawler.isProbablyHtml("http://koczewski.de"));
		assertTrue(WebCrawler.isProbablyHtml("http://koczewski.de/"));
		assertTrue(WebCrawler.isProbablyHtml("http://koczewski.de/index.html"));
		assertTrue(WebCrawler.isProbablyHtml("http://koczewski.de/index.php"));
		assertTrue(WebCrawler.isProbablyHtml("http://koczewski.de/index.jsp"));
		assertFalse(WebCrawler.isProbablyHtml("http://koczewski.de/image.png"));
	}

	@Test
	public void crawl() {
		WebCrawler wc = new WebCrawler();
		wc.crawl(new File("etc/WebCrawler.html").toURI().toString());
	}

	@Test
	public void download() {
		WebCrawler.download("http://koczewski.de", OUTPUT_DIR + "/webcrawler");
	}

}
