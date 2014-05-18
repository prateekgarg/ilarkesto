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
import ilarkesto.mda.legacy.model.EntityModel;

import java.util.Collection;

public class EntityIndexGenerator extends AClassGenerator {

	private Collection<EntityModel> entities;
	private String packageName;

	public EntityIndexGenerator(Collection<EntityModel> entities, String packageName) {
		super();
		this.entities = entities;
		this.packageName = packageName;
	}

	@Override
	protected void writeContent() {
		ln();
		ln("    public static List<Class<? extends " + AEntity.class.getName()
				+ ">> types = new ArrayList<Class<? extends " + AEntity.class.getName() + ">>();");

		ln();
		ln("    static {");
		for (EntityModel entity : entities) {
			ln("        types.add(" + entity.getBeanClass() + ".class);");
		}
		ln("    }");
	}

	@Override
	protected String getPackage() {
		return packageName;
	}

	@Override
	protected String getName() {
		return "GEntityIndex";
	}

	@Override
	protected boolean isAbstract() {
		return false;
	}

	@Override
	protected boolean isInterface() {
		return false;
	}

	@Override
	protected boolean isOverwrite() {
		return true;
	}

}
