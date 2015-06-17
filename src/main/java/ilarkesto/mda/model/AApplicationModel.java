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
package ilarkesto.mda.model;

import ilarkesto.core.base.ToStringComparator;
import ilarkesto.core.base.Utl;
import ilarkesto.core.persistance.AEntity;
import ilarkesto.mda.legacy.model.EntityModel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AApplicationModel {

	private String basePackageForEntites;

	public AApplicationModel(String basePackageForEntites) {
		super();
		this.basePackageForEntites = basePackageForEntites;
	}

	protected final EntityModel createEntity(String name, String packageName, boolean selfcontained) {
		EntityModel model = new EntityModel(name, basePackageForEntites + "." + packageName);
		model.setSuperclass(AEntity.class.getName());
		model.setSelfcontained(selfcontained);
		return model;
	}

	public final List<EntityModel> getEntityModels() {
		List<EntityModel> ret = new ArrayList<EntityModel>();
		List<Method> methods = Arrays.asList(getClass().getMethods());
		Utl.sort(methods, ToStringComparator.INSTANCE);
		for (Method method : methods) {
			if (!method.getName().startsWith("get")) continue;
			if (!method.getReturnType().equals(EntityModel.class)) continue;
			EntityModel entityModel;
			try {
				entityModel = (EntityModel) method.invoke(this);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
			ret.add(entityModel);
		}
		Utl.sort(ret);
		return ret;
	}

	public String getBasePackageForEntites() {
		return basePackageForEntites;
	}

}
