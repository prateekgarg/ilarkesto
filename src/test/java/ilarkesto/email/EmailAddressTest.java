package ilarkesto.email;

import ilarkesto.testng.ATest;

import java.util.List;

import org.testng.annotations.Test;

public class EmailAddressTest extends ATest {

	@Test
	public void parse() {
		assertEmail(new EmailAddress("wi@koczewski.de"), null, "wi@koczewski.de");
		assertEmail(new EmailAddress("Witoslaw Koczewski <wi@koczewski.de>"), "Witoslaw Koczewski", "wi@koczewski.de");
		assertEmail(new EmailAddress("\"Witoslaw Koczewski\" <wi@koczewski.de>"), "Witoslaw Koczewski",
			"wi@koczewski.de");
		assertEmail(new EmailAddress("'Witoslaw Koczewski' <wi@koczewski.de>"), "Witoslaw Koczewski", "wi@koczewski.de");
	}

	@Test
	public void parseList() {
		List<EmailAddress> addresses = EmailAddress
				.parseList("Witoslaw Koczewski <wi@koczewski.de>; support@kunagi.org");
		assertEquals(addresses.size(), 2);
		assertEmail(addresses.get(0), "Witoslaw Koczewski", "wi@koczewski.de");
		assertEmail(addresses.get(1), null, "support@kunagi.org");
	}

	private static void assertEmail(EmailAddress ea, String label, String address) {
		assertEquals(ea.getLabel(), label);
		assertEquals(ea.getAddress(), address);
	}

}
