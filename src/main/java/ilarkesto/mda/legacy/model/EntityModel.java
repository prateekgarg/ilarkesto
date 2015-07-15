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
package ilarkesto.mda.legacy.model;

import ilarkesto.base.Str;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class EntityModel extends DatobModel {

	private List<ActionModel> actions = new ArrayList<ActionModel>();
	private boolean ownable;
	private EntityModel userModel;
	private boolean viewProtected = false;
	private boolean editProtected;
	private List<BackReferenceModel> backReferences = new ArrayList<BackReferenceModel>();
	private List<ComputedValueModel> computedValues = new ArrayList<ComputedValueModel>();
	private boolean singleton;
	private boolean selfcontained = true;

	public EntityModel(String name, String packageName) {
		super(name, packageName);
	}

	public ComputedValueModel addComputedValue(String name, Class type) {
		ComputedValueModel computedValue = new ComputedValueModel(name);
		computedValue.setReturnType(type);
		computedValues.add(computedValue);
		return computedValue;
	}

	public ComputedValueModel addComputedValue(String name, String type) {
		ComputedValueModel computedValue = new ComputedValueModel(name);
		computedValue.setReturnType(type);
		computedValues.add(computedValue);
		return computedValue;
	}

	public List<ComputedValueModel> getComputedValues() {
		return computedValues;
	}

	public Set<PropertyModel> getSlaveProperties() {
		Set<PropertyModel> ret = new LinkedHashSet<PropertyModel>();
		for (PropertyModel p : getProperties()) {
			if (p.isSlave()) ret.add(p);
		}
		return ret;
	}

	public boolean isSelfcontained() {
		return selfcontained;
	}

	public EntityModel setSelfcontained(boolean selfcontained) {
		this.selfcontained = selfcontained;
		return this;
	}

	public boolean isSingleton() {
		return singleton;
	}

	public EntityModel setSingleton(boolean singleton) {
		this.singleton = singleton;
		return this;
	}

	public void addBackReference(BackReferenceModel backReference) {
		if (backReferences.contains(backReference))
			throw new IllegalArgumentException("backReference already exists: " + backReference.getName());
		backReferences.add(backReference);
	}

	public List<BackReferenceModel> getBackReferences() {
		return backReferences;
	}

	public List<ActionModel> getActions() {
		return actions;
	}

	public ActionModel addAction(String name) {
		ActionModel action = new ActionModel(name, getPackageName());
		action.addParameter(Str.lowercaseFirstLetter(getName()), this);
		actions.add(action);
		return action;
	}

	@Override
	public boolean isEntity() {
		return true;
	}

	@Override
	public boolean isValueObject() {
		return false;
	}

	public String getDaoName() {
		return Str.lowercaseFirstLetter(getName()) + "Dao";
	}

	public String getDaoClass() {
		return getBeanClass() + "Dao";
	}

	public String getAbstractBaseDaoClassName() {
		return getAbstractBaseClassName() + "Dao";
	}

	public final boolean isOwnable() {
		BeanModel superbean = getSuperbean();
		if (superbean != null) {
			if (superbean instanceof EntityModel) {
				if (((EntityModel) superbean).isOwnable()) return true;
			}
		}
		return ownable;
	}

	public final void setOwnable(boolean ownable) {
		if (userModel == null) throw new RuntimeException("userModel==null");
		this.ownable = ownable;
	}

	public void setUserModel(EntityModel userModel) {
		this.userModel = userModel;
	}

	public EntityModel getUserModel() {
		return userModel;
	}

	public final boolean isViewProtected() {
		BeanModel superbean = getSuperbean();
		if (superbean != null) {
			if (superbean instanceof EntityModel) {
				if (((EntityModel) superbean).isViewProtected()) return true;
			}
		}
		return viewProtected;
	}

	public final void setViewProtected(boolean viewProtected) {
		if (userModel == null) throw new RuntimeException("userModel==null");
		this.viewProtected = viewProtected;
	}

	public final boolean isEditProtected() {
		BeanModel superbean = getSuperbean();
		if (superbean != null) {
			if (superbean instanceof EntityModel) {
				if (((EntityModel) superbean).isEditProtected()) return true;
			}
		}

		return editProtected;
	}

	public final void setEditProtected(boolean editProtected) {
		if (userModel == null) throw new RuntimeException("userModel==null");
		this.editProtected = editProtected;
	}

	private boolean deleteProtected;

	public final boolean isDeleteProtected() {
		BeanModel superbean = getSuperbean();
		if (superbean != null) {
			if (superbean instanceof EntityModel) {
				if (((EntityModel) superbean).isDeleteProtected()) return true;
			}
		}

		return deleteProtected;
	}

	public final void setDeleteProtected(boolean deleteProtected) {
		if (userModel == null) throw new RuntimeException("userModel==null");
		this.deleteProtected = deleteProtected;
	}

	@Override
	public int compareTo(AModel o) {
		return super.compareTo(o);
	}

	public boolean containsBackReference(String name) {
		for (BackReferenceModel br : backReferences) {
			if (br.getName().equals(name)) return true;
		}
		return false;
	}

	public PropertyModel getProperty(String name, boolean mandatory) {
		for (PropertyModel property : getProperties()) {
			if (property.getName().equals(name)) return property;
		}
		if (!mandatory) return null;
		throw new IllegalStateException("Property does not exist: " + name);
	}

}
