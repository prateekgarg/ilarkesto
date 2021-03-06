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
package ilarkesto.integration.graphviz;

import ilarkesto.id.CountingIdGenerator;
import ilarkesto.id.IdGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph extends ANodesContainer {

	IdGenerator idGenerator = new CountingIdGenerator("e");
	private List<Edge> edges = new ArrayList<Edge>();
	private Map<String, String> properties = new HashMap<String, String>();

	public Graph setRankdirLR() {
		return setRankdir("LR");
	}

	public Graph setRankdir(String rankdir) {
		return property("rankdir", rankdir);
	}

	public Graph property(String name, String value) {
		if (value != null) properties.put(name, value);
		return this;
	}

	@Override
	public Cluster cluster(String label) {
		Cluster cluster = new Cluster(this, idGenerator.generateId());
		subgraphs.add(cluster);
		if (label != null) cluster.label(label);
		return cluster;
	}

	@Override
	public Node node(String label) {
		Node node = createNode(label);
		nodes.add(node);
		return node;
	}

	Node createNode(String label) {
		Node node = new Node(idGenerator.generateId());
		if (label != null) node.label(label);
		return node;
	}

	public Edge edge(Node from, Node to) {
		Edge edge = new Edge(from, to);
		edges.add(edge);
		return edge;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("digraph G {\n");

		for (Map.Entry<String, String> entry : properties.entrySet()) {
			sb.append("  ").append(entry.getKey()).append(" = ").append(entry.getValue()).append(";\n");
		}

		for (Cluster subgraph : subgraphs) {
			sb.append(subgraph.toString()).append("\n");
		}
		for (Node node : nodes)
			sb.append("  ").append(node).append("\n");
		for (Edge edge : edges) {
			sb.append("  ").append(edge).append("\n");
		}
		sb.append("}\n");
		return sb.toString();
	}
}
