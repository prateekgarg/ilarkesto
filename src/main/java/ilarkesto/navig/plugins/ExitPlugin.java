package ilarkesto.navig.plugins;

import ilarkesto.core.navig.Item;
import ilarkesto.core.navig.Navigator;
import ilarkesto.core.navig.Plugin;

public class ExitPlugin implements Plugin {

	@Override
	public void initialize(Navigator navigator) {
		navigator.getPage().add(new Item(this, "Exit"));
	}

	@Override
	public void execute(Navigator navigator, Item item) {
		System.exit(0);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
