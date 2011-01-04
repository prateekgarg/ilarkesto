package ilarkesto.navig.swing;

import ilarkesto.core.navig.Item;
import ilarkesto.core.navig.Navigator;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class ListPagePanel extends JPanel {

	private Navigator navigator;
	private Font itemFont;
	private DefaultListModel model = new DefaultListModel();
	private JList list;
	private List<Item> items;

	public ListPagePanel(Navigator navigator) {
		this.navigator = navigator;

		itemFont = new Font("Arial", Font.BOLD, 16);
		list = new JList(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setFont(itemFont);
		list.addKeyListener(new ListKeyListener());

		items = navigator.getPage().getItems();
		for (Item item : items) {
			model.addElement(item.getLabel());
		}
		list.getSelectionModel().setSelectionInterval(0, 0);

		setLayout(new GridLayout(1, 1));
		add(new JScrollPane(list));
	}

	@Override
	public void grabFocus() {
		list.grabFocus();
	}

	public Item getSelectedItem() {
		int idx = list.getSelectedIndex();
		if (idx < 0) return null;
		return items.get(idx);
	}

	class ListKeyListener extends KeyAdapter {

		@Override
		public void keyTyped(KeyEvent ev) {
			char key = ev.getKeyChar();
			// Log.DEBUG("key ->", new Integer(key));
			if (key == 13 || key == 10 || key == 32) {
				navigator.execute(getSelectedItem());
				return;
			}
			if (key == 27 || key == 8) {
				navigator.goBack();
				return;
			}
		}
	}

}
