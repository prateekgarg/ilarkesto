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
package ilarkesto.mda.legacy.generator;

import ilarkesto.auth.DeleteProtected;
import ilarkesto.auth.EditProtected;
import ilarkesto.auth.Ownable;
import ilarkesto.auth.ViewProtected;
import ilarkesto.base.Str;
import ilarkesto.core.logging.Log;
import ilarkesto.core.persistance.AEntityQuery;
import ilarkesto.core.persistance.AllByTypeQuery;
import ilarkesto.core.time.Date;
import ilarkesto.core.time.DateAndTime;
import ilarkesto.core.time.Time;
import ilarkesto.mda.legacy.model.BackReferenceModel;
import ilarkesto.mda.legacy.model.EntityModel;
import ilarkesto.mda.legacy.model.PropertyModel;
import ilarkesto.persistence.ADatob;
import ilarkesto.persistence.AEntity;
import ilarkesto.search.Searchable;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class EntityGenerator extends DatobGenerator<EntityModel> {

	public EntityGenerator(EntityModel bean) {
		super(bean);
	}

	@Override
	protected void writeContent() {
		ln();
		ln("    protected static final " + Log.class.getName() + " log = " + Log.class.getName() + ".get("
				+ bean.getName() + ".class);");

		if (isLegacyBean(bean)) {
			String daoName = Str.lowercaseFirstLetter(bean.getDaoName());

			if (!bean.isAbstract()) {
				ln();
				comment(AEntity.class.getSimpleName());
				ln();
				s("    public final " + bean.getDaoClass() + " getDao() {").ln();
				s("        return " + daoName + ";").ln();
				s("    }").ln();
			}

			ln();

			ln("    protected void repairDeadDatob(" + ADatob.class.getSimpleName() + " datob) {");
			for (PropertyModel p : bean.getProperties()) {
				if (!p.isValueObject()) continue;
				if (p.isCollection()) {
					ln("        if (" + getFieldName(p) + ".contains(datob)) {");
					ln("            " + getFieldName(p) + ".remove(datob);");
					ln("            fireModified(\"" + p.getName() + "-=\" + datob);");
					ln("        }");
				} else {
					ln("        if (valueObject.equals(" + getFieldName(p) + ")) {");
					ln("        " + getFieldName(p) + " = null;");
					ln("            fireModified(\"" + p.getName() + "=null\");");
					ln("        }");
				}
			}
			ln("    }");
		}

		if (!isLegacyBean(bean)) {
			String queryName = "A" + bean.getName() + "Query";

			ln();
			ln("    public static List<" + bean.getName() + "> listAll() {");
			ln("        return new " + AllByTypeQuery.class.getName() + "(" + bean.getName() + ".class).list();");
			ln("    }");

			ln();
			ln("    public static", bean.getName(), "getById(String id) {");
			ln("        return (" + bean.getName() + ") entityResolver.get(id);");
			ln("    }");

			for (PropertyModel p : bean.getProperties()) {
				ln();
				if (p.isUnique()) {
					ln("    public static", bean.getName(), "getBy" + Str.uppercaseFirstLetter(p.getName()) + "(final "
							+ p.getType() + " " + p.getName() + ") {");
					ln("        return (" + bean.getName() + ") entityResolver.get(new " + queryName + "() {");
					ln("            @Override");
					ln("            public boolean matches(" + bean.getName() + " entity) {");
					ln("                return entity.is" + Str.uppercaseFirstLetter(p.getName()) + "(" + p.getName()
							+ ");");
					ln("            }");
					ln("        });");
					ln("    }");
				} else {
					ln("    public static List<", bean.getName() + ">",
						"listBy" + Str.uppercaseFirstLetter(p.getName()) + "(final " + p.getType() + " " + p.getName()
								+ ") {");
					ln("        return new " + queryName + "() {");
					ln("            @Override");
					ln("            public boolean matches(" + bean.getName() + " entity) {");
					ln("                return entity.is" + Str.uppercaseFirstLetter(p.getName()) + "(" + p.getName()
							+ ");");
					ln("            }");
					ln("        }.list();");
					ln("    }");
				}
			}

			ln();
			ln("    public abstract static class A" + bean.getName() + "Query extends " + AEntityQuery.class.getName()
					+ "<" + bean.getName() + "> {");
			ln("        public Class<" + bean.getName() + "> getType() {");
			ln("            return " + bean.getName() + ".class;");
			ln("        }");
			ln("    }");
		}

		ln();
		ln("    @Override");
		ln("    public void storeProperties(Map properties) {");
		ln("        super.storeProperties(properties);");
		for (PropertyModel p : bean.getProperties()) {
			if (p.isCollection()) {
				String propertyVar = p.isReference() ? p.getName() + "Ids" : p.getName();
				ln("        properties.put(\"" + propertyVar + "\", this." + propertyVar + ");");
			} else {
				String propertyVar = p.isReference() ? p.getName() + "Id" : p.getName();
				if (p.getType().equals(Date.class.getName())) {
					ln("        properties.put(\"" + propertyVar + "\", this." + propertyVar
							+ " == null ? null : this." + propertyVar + ".toString());");
				} else if (p.getType().equals(Time.class.getName())) {
					ln("        properties.put(\"" + propertyVar + "\", this." + propertyVar
							+ " == null ? null : this." + propertyVar + ".toString());");
				} else if (p.getType().equals(DateAndTime.class.getName())) {
					ln("        properties.put(\"" + propertyVar + "\", this." + propertyVar
							+ " == null ? null : this." + propertyVar + ".toString());");
				} else {
					ln("        properties.put(\"" + propertyVar + "\", this." + propertyVar + ");");
				}
			}
		}
		ln("    }");

		// if (bean.isGwtSupport()) {
		// String dtoType = getPackage().replace(".server", ".client") + ".G" + bean.getName() + "Dto";
		// ln();
		// ln("    public " + dtoType + " createDto() {");
		// ln("        " + dtoType + " dto = new " + dtoType + "();");
		// for (PropertyModel p : bean.getProperties()) {
		// if (p.isCollection()) {
		// String propertyVar = p.isReference() ? p.getName() + "Ids" : p.getName();
		// ln("        dto." + propertyVar + ".addAll(this." + propertyVar + ");");
		// } else {
		// String propertyVar = p.isReference() ? p.getName() + "Id" : p.getName();
		// if (p.getType().equals(Date.class.getName())) {
		// ln("        properties.put(\"" + propertyVar + "\", this." + propertyVar
		// + " == null ? null : this." + propertyVar + ".toString());");
		// } else {
		// ln("        properties.put(\"" + propertyVar + "\", this." + propertyVar + ");");
		// }
		// }
		// }
		// ln("        return dto;");
		// ln("    }");
		// }

		if (!bean.isAbstract()) {
			ln();
			ln("    public int compareTo(" + bean.getName() + " other) {");
			ln("        return toString().toLowerCase().compareTo(other.toString().toLowerCase());");
			ln("    }");
		}

		Set<String> backRefs = new HashSet<String>();
		for (BackReferenceModel br : bean.getBackReferences()) {
			if (backRefs.contains(br.getName())) continue;
			backRefs.add(br.getName());
			writeBackReference(br);
		}

		super.writeContent();
	}

	private void writeBackReference(BackReferenceModel br) {
		ln();
		PropertyModel ref = br.getReference();
		EntityModel refEntity = ref.getEntity();
		String by = Str.uppercaseFirstLetter(ref.getName());
		if (ref.isCollection()) by = Str.removeSuffix(by, "s");
		if (ref.isUnique()) {
			ln("    public final " + refEntity.getBeanClass() + " get" + Str.uppercaseFirstLetter(br.getName())
					+ "() {");
			if (isLegacyBean(refEntity)) {
				ln("        return " + Str.lowercaseFirstLetter(refEntity.getName()) + "Dao.get"
						+ Str.uppercaseFirstLetter(br.getName()) + "By" + by + "((" + bean.getName() + ")this);");
			} else {
				ln("        return " + refEntity.getBeanClass() + ".getBy" + by + "((" + bean.getName() + ")this);");
			}
			ln("    }");
		} else {
			if (isLegacyBean(refEntity)) {
				ln("    public final java.util.Set<" + refEntity.getBeanClass() + "> get"
						+ Str.uppercaseFirstLetter(br.getName()) + "s() {");
				ln("        return " + Str.lowercaseFirstLetter(refEntity.getName()) + "Dao.get" + refEntity.getName()
						+ "sBy" + by + "((" + bean.getName() + ")this);");
				ln("    }");
			} else {
				ln("    public final List<" + refEntity.getBeanClass() + "> get"
						+ Str.uppercaseFirstLetter(br.getName()) + "s() {");
				ln("        return " + refEntity.getBeanClass() + ".listBy" + by + "((" + bean.getName() + ")this);");
				ln("    }");
			}
		}
	}

	@Override
	protected Set<String> getSuperinterfaces() {
		Set<String> result = new LinkedHashSet<String>();
		result.addAll(super.getSuperinterfaces());
		if (bean.isViewProtected()) result.add(ViewProtected.class.getName() + "<" + getUserClassName() + ">");
		if (bean.isEditProtected()) result.add(EditProtected.class.getName() + "<" + getUserClassName() + ">");
		if (bean.isDeleteProtected()) result.add(DeleteProtected.class.getName() + "<" + getUserClassName() + ">");
		if (bean.isOwnable()) result.add(Ownable.class.getName() + "<" + getUserClassName() + ">");
		if (!bean.isAbstract()) result.add(Comparable.class.getName() + "<" + bean.getName() + ">");
		if (isLegacyBean(bean)) {
			if (bean.isSearchable()) result.add(Searchable.class.getName());
		}
		return result;
	}

	protected final String getUserClassName() {
		EntityModel userModel = bean.getUserModel();
		if (userModel == null && bean.getName().equals("User")) userModel = bean;
		if (userModel == null) return null;
		return userModel.getPackageName() + "." + userModel.getName();
	}

	@Override
	protected void writeDependencies() {
		super.writeDependencies();
		if (isLegacyBean(bean)) {
			String daoName = Str.lowercaseFirstLetter(bean.getDaoName());
			if (isLegacyBean(bean)) {
				if (!bean.isAbstract() && !bean.containsDependency(daoName)) {
					dependency(bean.getDaoClass(), daoName, true, false);
				}
			}
			Set<String> refDaos = new HashSet<String>();
			for (BackReferenceModel br : bean.getBackReferences()) {
				EntityModel refEntity = br.getReference().getEntity();
				String refDaoName = refEntity.getDaoName();
				if (refDaoName.equals(daoName)) continue;
				if (refDaos.contains(refDaoName)) continue;
				refDaos.add(refDaoName);
				if (bean.containsDependency(refDaoName)) continue;
				dependency(refEntity.getDaoClass(), refDaoName, true, false);
			}
		}
	}

	@Override
	protected void writeCollectionProperty(PropertyModel p) {
		super.writeCollectionProperty(p);

		// --- isOwner ---
		if ("owners".equals(p.getName()) && bean.isOwnable()) {
			ln();
			ln("    public final boolean isOwner(" + getUserClassName() + " user) {");
			ln("        return " + getFieldName(p) + ".contains(user.getId());");
			ln("    }");
		}

		// --- setOwner ---
		if ("owners".equals(p.getName())) {
			ln();
			ln("    public void setOwner(" + getUserClassName() + " owner) {");
			ln("        clearOwners();");
			ln("        if (owner != null) addOwner((" + p.getContentType() + ")owner);");
			ln("    }");
		}
	}

	@Override
	protected boolean isCopyConstructorEnabled() {
		return false;
	}

}
