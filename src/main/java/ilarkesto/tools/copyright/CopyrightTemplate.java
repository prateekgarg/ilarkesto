package ilarkesto.tools.copyright;

public interface CopyrightTemplate {

	boolean containsText(String content);

	String getText(String years, String... owners);

}
