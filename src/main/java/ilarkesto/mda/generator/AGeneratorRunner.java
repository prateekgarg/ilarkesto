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
package ilarkesto.mda.generator;

import ilarkesto.core.logging.Log;
import ilarkesto.integration.graphviz.Graph;
import ilarkesto.integration.graphviz.Graphviz;
import ilarkesto.integration.graphviz.Node;
import ilarkesto.io.IO;
import ilarkesto.mda.legacy.generator.EntityGenerator;
import ilarkesto.mda.legacy.generator.EntityIndexGenerator;
import ilarkesto.mda.legacy.model.EntityModel;
import ilarkesto.mda.legacy.model.PropertyModel;
import ilarkesto.mda.legacy.model.ReferencePropertyModel;
import ilarkesto.mda.legacy.model.ReferenceSetPropertyModel;
import ilarkesto.mda.model.AApplicationModel;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class AGeneratorRunner<M extends AApplicationModel> {

	protected final Log log = Log.get(getClass());

	protected M applicationModel;

	public AGeneratorRunner(M applicationModel) {
		super();
		this.applicationModel = applicationModel;
	}

	public void generateEntities() {
		for (EntityModel entityModel : applicationModel.getEntityModels()) {
			new EntityGenerator(entityModel).generate();
		}
	}

	public void generateEntitiesIndex() {
		new EntityIndexGenerator(applicationModel.getEntityModels(), applicationModel.getBasePackageForEntites()
				+ ".base").generate();
	}

	public void generateGraphviz() {
		Graph g = new Graph();
		Map<String, Node> nodesByEntityName = new LinkedHashMap<String, Node>();
		for (EntityModel entity : applicationModel.getEntityModels()) {
			Node nEntity = g.node(entity.getName());
			nodesByEntityName.put(entity.getName(), nEntity);

		}
		for (EntityModel entity : applicationModel.getEntityModels()) {
			for (PropertyModel property : entity.getProperties()) {
				if (property instanceof ReferencePropertyModel) {
					ReferencePropertyModel ref = (ReferencePropertyModel) property;
					EntityModel referencedEntity = ref.getReferencedEntity();
					Node nFrom = nodesByEntityName.get(entity.getName());
					Node nTo = nodesByEntityName.get(referencedEntity.getName());
					g.edge(nFrom, nTo).label("1 " + ref.getName());
				}
				if (property instanceof ReferenceSetPropertyModel) {
					ReferenceSetPropertyModel ref = (ReferenceSetPropertyModel) property;
					EntityModel referencedEntity = ref.getReferencedEntity();
					Node nFrom = nodesByEntityName.get(entity.getName());
					Node nTo = nodesByEntityName.get(referencedEntity.getName());
					g.edge(nFrom, nTo).label("n " + ref.getName());
				}
			}
		}

		File dotFile = new File("etc/entities.dot");
		String dot = g.toString();
		if (dotFile.exists()) {
			String old = IO.readFile(dotFile);
			if (dot.equals(old)) return;
		}
		log.info("Writing Graphvis file:", dotFile);
		IO.writeFile(dotFile, dot, IO.UTF_8);
		File pngFile = new File("runtimedata/entities.png");
		IO.move(Graphviz.createPng(dot), pngFile, true);
		log.info("Graph created as PNG:", pngFile);
	}

}
