package ilarkesto.core.navig;

import ilarkesto.testng.ATest;

import org.testng.annotations.Test;

public class NavigatorTest extends ATest {

	@Test
	public void goBack() {
		Navigator n = new Navigator();
		Page p = new Page(null);
		n.goNext(p);
		n.goBack();
		assertSame(n.getPage(), n.getRootPage());
	}

	@Test
	public void goNext() {
		Navigator n = new Navigator();
		Page p = new Page(null);
		n.goNext(p);
		assertSame(n.getPage(), p);
	}

	@Test
	public void goBackToRoot() {
		Navigator n = new Navigator();
		n.goBackToRoot();
		assertSame(n.getPage(), n.getRootPage());
	}

}
