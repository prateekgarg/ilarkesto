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
package ilarkesto.mda.legacy.generator;

import ilarkesto.core.persistance.AEntity;
import ilarkesto.gwt.client.persistence.AGwtEntityFactory;
import ilarkesto.mda.legacy.model.EntityModel;

import java.util.Collection;

public class GwtEntityFactoryGenerator extends AClassGenerator {

	private Collection<EntityModel> entities;
	private String packageName;

	public GwtEntityFactoryGenerator(Collection<EntityModel> entities, String packageName) {
		super();
		this.entities = entities;
		this.packageName = packageName;
	}

	@Override
	protected void writeContent() {
		ln();
		annotationOverride();
		ln("    public", AEntity.class.getName(), "createEntity(String type, String id) {");
		for (EntityModel entity : entities) {
			ln("        if (type.equals(\"" + entity.getName() + "\")) return new", entity.getBeanClass()
					+ "().setId(id);");
		}
		ln("        throw new IllegalStateException(\"Unsupported entity: \" + type);");
		ln("    }");
	}

	@Override
	protected String getName() {
		return "GwtEntityFactory";
	}

	@Override
	protected String getSuperclass() {
		return AGwtEntityFactory.class.getName();
	}

	@Override
	protected String getPackage() {
		return packageName;
	}

	@Override
	protected boolean isInterface() {
		return false;
	}

	@Override
	protected boolean isAbstract() {
		return false;
	}

	@Override
	protected boolean isOverwrite() {
		return true;
	}

}
