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

import java.util.HashMap;
import java.util.Map;

public class Cluster extends ANodesContainer {

	private Graph graph;
	private String name;

	private Map<String, String> properties = new HashMap<String, String>();

	public Cluster(Graph graph, String name) {
		super();
		this.graph = graph;
		this.name = "cluster_" + name;
	}

	public Cluster label(String label) {
		return property("label", "\"" + label + "\"");
	}

	public Cluster styleFilled(String color) {
		style("filled");
		return color(color);
	}

	private Cluster color(String color) {
		return property("color", color);
	}

	public Cluster style(String style) {
		return property("style", style);
	}

	private Cluster property(String name, String value) {
		if (value != null) properties.put(name, value);
		return this;
	}

	@Override
	public Cluster cluster(String label) {
		Cluster subgraph = new Cluster(graph, graph.idGenerator.generateId());
		subgraphs.add(subgraph);
		if (label != null) subgraph.label(label);
		return subgraph;
	}

	@Override
	public Node node(String label) {
		Node node = graph.createNode(label);
		nodes.add(node);
		return node;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("subgraph ").append(name).append(" {\n");
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			sb.append("  ").append(entry.getKey()).append(" = ").append(entry.getValue()).append(";\n");
		}

		for (Cluster subgraph : subgraphs) {
			sb.append(subgraph.toString()).append("\n");
		}

		for (Node node : nodes)
			sb.append("  ").append(node).append("\n");
		sb.append("}\n");
		return sb.toString();
	}
}
