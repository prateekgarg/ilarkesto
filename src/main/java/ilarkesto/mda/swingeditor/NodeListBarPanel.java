/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>, Artjom Kochtchi
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.mda.swingeditor;

import ilarkesto.core.logging.Log;
import ilarkesto.core.scope.In;
import ilarkesto.core.scope.Init;
import ilarkesto.core.scope.Scope;
import ilarkesto.mda.model.ModellingSession;
import ilarkesto.mda.model.Node;
import ilarkesto.mda.swingeditor.NodeListPanel.Observer;
import ilarkesto.swing.HorizontalBarPanel;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class NodeListBarPanel extends JPanel implements Observer {

	private static Log log = Log.get(NodeListBarPanel.class);

	@In
	ModellingSession modellingSession;

	@In
	SwingModelHelper swingModelHelper;

	private HorizontalBarPanel bar;

	public NodeListBarPanel() {
		bar = new HorizontalBarPanel();

		setLayout(new BorderLayout());
		add(bar, BorderLayout.CENTER);
		setBorder(new EmptyBorder(10, 10, 10, 10));
	}

	@Init
	public void rebuild() {
		log.debug("rebuild()");
		bar.removeAllColumns();
		bar.addColumn(createNodeList(modellingSession.getModel().getRoot()));
	}

	public void onModelChanged() {
		rebuild();
	}

	private NodeListPanel createNodeList(Node node) {
		NodeListPanel nodeList = new NodeListPanel(swingModelHelper);
		nodeList.setObserver(this);
		nodeList.setNode(node);
		return nodeList;
	}

	@Override
	public void onNodeSelectionChanged(NodeListPanel nodeListPanel, Node selectedNode) {
		Scope.get().getComponent(NodeValuePanel.class).onNodeSelectionChanged(selectedNode);
		bar.removeColumnsAfter(nodeListPanel);
		if (selectedNode != null) {
			if (!selectedNode.containsChildren()) {
				if (!modellingSession.getRuleSet().containsAllowedChildTypes(selectedNode)) return;
			}
			bar.addColumn(createNodeList(selectedNode));
		}
	}

}
