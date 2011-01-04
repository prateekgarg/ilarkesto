package ilarkesto.core.navig;

public interface Plugin {

	void initialize(Navigator navigator);

	void execute(Navigator navigator, Item item);

}
