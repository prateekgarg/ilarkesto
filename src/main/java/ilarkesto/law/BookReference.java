package ilarkesto.law;

public class BookReference {

	private String code;
	private String title;

	private BookReference(String code, String title) {
		super();
		this.code = code;
		this.title = title;
	}

	public String getCode() {
		return code;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public String toString() {
		return getCode();
	}

}
