package ilarkesto.navig.swing;

import ilarkesto.core.navig.Navigator;
import ilarkesto.core.navig.NavigatorObserver;
import ilarkesto.navig.plugins.ExitPlugin;
import ilarkesto.navig.plugins.FileBrowserPlugin;
import ilarkesto.swing.Swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;

public class NavigatorPanel extends JPanel implements NavigatorObserver {

	public static void main(String[] args) {
		Navigator n = new Navigator();
		n.addPlugin(new FileBrowserPlugin());
		n.addPlugin(new ExitPlugin());
		NavigatorPanel np = new NavigatorPanel(n);
		np.setPreferredSize(Swing.getFractionFromScreen(0.8d, 0.8d));
		Swing.showInJFrame(np);
	}

	private Navigator navigator;
	private JPanel pageWrapper;

	public NavigatorPanel(Navigator navigator) {
		super(new BorderLayout());
		this.navigator = navigator;
		navigator.setObserver(this);

		pageWrapper = new JPanel(new GridLayout(1, 1));
		add(pageWrapper, BorderLayout.CENTER);

		update();
	}

	protected void update() {
		ListPagePanel pagePanel = new ListPagePanel(navigator);
		pageWrapper.removeAll();
		pageWrapper.add(pagePanel);
		pageWrapper.updateUI();
		pagePanel.grabFocus();
	}

	@Override
	public void onPageChanged(Navigator navigator) {
		assert this.navigator == navigator;
		update();
	}

}
