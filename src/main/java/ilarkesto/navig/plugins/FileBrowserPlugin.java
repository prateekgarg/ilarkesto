package ilarkesto.navig.plugins;

import ilarkesto.base.Env;
import ilarkesto.core.base.Str;
import ilarkesto.core.navig.Item;
import ilarkesto.core.navig.Navigator;
import ilarkesto.core.navig.Page;
import ilarkesto.core.navig.Plugin;

import java.io.File;

public class FileBrowserPlugin implements Plugin {

	private Item rootItem;

	public FileBrowserPlugin() {
		rootItem = new Item(this, "File Browser");
	}

	@Override
	public void initialize(Navigator navigator) {
		navigator.getRootPage().add(rootItem);
	}

	@Override
	public void execute(Navigator navigator, Item item) {
		if (item == rootItem) {
			Page page = createPage(File.listRoots());
			page.setLabel(rootItem.getLabel());
			navigator.goNext(page);
			return;
		}
		File file = (File) item.getPayload();
		if (file.isDirectory()) {
			Page page = createPage(file.listFiles());
			page.setLabel(file.getName());
			navigator.goNext(page);
			return;
		}
		Env.get().executeFile(file, false);
	}

	private Page createPage(File[] files) {
		Page page = new Page(this);
		for (File file : files) {
			page.add(createItem(file));
		}
		return page;
	}

	private Item createItem(File file) {
		String name = file.getName();
		if (Str.isBlank(name)) name = file.getPath();
		Item item = new Item(this, name);
		item.setPayload(file);
		return item;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
