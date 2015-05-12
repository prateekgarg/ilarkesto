/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package ilarkesto.integration.graphviz;

import ilarkesto.core.logging.Log;

import java.util.HashMap;
import java.util.Map;

public abstract class AGraphBuilder {

	private Log log = Log.get(getClass());

	protected Graph graph;
	private Map<Object, Cluster> clustersByObject;
	private Map<Object, Node> nodesByObject;

	protected abstract void onBuild();

	protected abstract Object getClusterParentObject(Object object);

	public Edge edge(Object from, Object to) {
		Node nFrom = getNode(from);
		Node nTo = getNode(to);
		return graph.edge(nFrom, nTo);
	}

	public final Node getNode(Object object) {
		if (object == null) return null;
		if (object instanceof Node) return (Node) object;
		Node node = nodesByObject.get(object);
		if (node == null) {
			node = createNode(object);
			nodesByObject.put(object, node);
		}
		return node;
	}

	private final Node createNode(Object object) {
		Object clusterObject = getClusterParentObject(object);
		ANodesContainer container = clusterObject == null ? graph : getCluster(clusterObject);
		Node node = container.node(getLabel(object));
		return node;
	}

	public final Cluster getCluster(Object object) {
		if (object == null) return null;
		Cluster cluster = clustersByObject.get(object);
		if (cluster == null) {
			cluster = createCluster(object);
			clustersByObject.put(object, cluster);
		}
		return cluster;
	}

	private Cluster createCluster(Object object) {
		Object clusterObject = getClusterParentObject(object);
		ANodesContainer container = clusterObject == null ? graph : getCluster(clusterObject);
		Cluster cluster = container.cluster(getLabel(object));
		return cluster;
	}

	protected String getLabel(Object object) {
		return object.getClass().getSimpleName() + ":\n" + object.toString();
	}

	public Graph build() {
		graph = new Graph();
		nodesByObject = new HashMap<Object, Node>();
		clustersByObject = new HashMap<Object, Cluster>();
		onBuild();
		return graph;
	}

	@Override
	public String toString() {
		return build().toString();
	}

}
