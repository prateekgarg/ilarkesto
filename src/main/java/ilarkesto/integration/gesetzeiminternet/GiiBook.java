package ilarkesto.integration.gesetzeiminternet;

import ilarkesto.law.Book;

public class GiiBook extends Book {

	private String reference;

	public GiiBook(String reference, String code, String title) {
		super(code, title);
		this.reference = reference;
	}

}
