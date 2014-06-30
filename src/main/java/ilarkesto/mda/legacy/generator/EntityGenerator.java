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
import ilarkesto.core.base.Uuid;
import ilarkesto.core.logging.Log;
import ilarkesto.core.money.Money;
import ilarkesto.core.persistance.AEntityQuery;
import ilarkesto.core.persistance.AllByTypeQuery;
import ilarkesto.core.persistance.EditableKeytableValue;
import ilarkesto.core.persistance.KeytableValue;
import ilarkesto.core.persistance.Transaction;
import ilarkesto.core.time.Date;
import ilarkesto.core.time.DateAndTime;
import ilarkesto.core.time.DayAndMonth;
import ilarkesto.core.time.Time;
import ilarkesto.mda.legacy.model.BackReferenceModel;
import ilarkesto.mda.legacy.model.EntityModel;
import ilarkesto.mda.legacy.model.PredicateModel;
import ilarkesto.mda.legacy.model.PropertyModel;
import ilarkesto.mda.legacy.model.ReferencePropertyModel;
import ilarkesto.persistence.ADatob;
import ilarkesto.persistence.AEntity;
import ilarkesto.search.Searchable;

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
					ln("            fireModified(\"" + p.getName() + "\", datob);");
					ln("        }");
				} else {
					ln("        if (valueObject.equals(" + getFieldName(p) + ")) {");
					ln("        " + getFieldName(p) + " = null;");
					ln("            fireModified(\"" + p.getName() + "\", null);");
					ln("        }");
				}
			}
			ln("    }");
		}

		if (!isLegacyBean(bean)) {
			writeKeytableFactoryMethod();
			writeKeytableGetLabel();
			writeListAll();
			writeGetByListBy();
			writePredicates();
			writeGetPassengers();
			writeQueryBaseclass();
			writeToString();
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
				} else if (p.getType().equals(DayAndMonth.class.getName())) {
					ln("        properties.put(\"" + propertyVar + "\", this." + propertyVar
							+ " == null ? null : this." + propertyVar + ".toString());");
				} else if (p.getType().equals(Money.class.getName())) {
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

		Set<String> backRefs = new LinkedHashSet<String>();
		for (BackReferenceModel br : bean.getBackReferences()) {
			if (backRefs.contains(br.getName())) continue;
			backRefs.add(br.getName());
			writeBackReference(br);
		}

		super.writeContent();
	}

	private void writeKeytableGetLabel() {
		for (PropertyModel p : bean.getProperties()) {
			if (!(p instanceof ReferencePropertyModel)) continue;
			ReferencePropertyModel ref = (ReferencePropertyModel) p;
			EntityModel referencedEntity = ref.getReferencedEntity();
			if (!referencedEntity.getSuperinterfaces().contains(KeytableValue.class.getName())
					&& !referencedEntity.getSuperinterfaces().contains(EditableKeytableValue.class.getName()))
				continue;
			ln();
			String pNameUpper = Str.uppercaseFirstLetter(p.getName());
			ln("    public String get" + pNameUpper + "Label() {");
			ln("        return is" + pNameUpper + "Set() ? get" + pNameUpper + "().getLabel() : null;");
			ln("    }");
		}
	}

	private void writeToString() {
		if (isKeytableValue()) {
			ln();
			annotationOverride();
			ln("    public String asString() {");
			ln("        return getLabel();");
			ln("    }");
			return;
		}
	}

	private boolean isKeytableValue() {
		Set<String> superinterfaces = bean.getSuperinterfaces();
		if (superinterfaces.contains(KeytableValue.class.getName())) return true;
		if (superinterfaces.contains(EditableKeytableValue.class.getName())) return true;
		return false;
	}

	private void writeKeytableFactoryMethod() {
		if (!isKeytableValue()) return;

		ln();
		ln("    public static synchronized", bean.getBeanClass(), " create(String key, String label) {");
		ln("        " + bean.getBeanClass(), " ktvalue = getByKey(key);");
		ln("        if (ktvalue != null) return ktvalue;");
		ln("        ktvalue = new", bean.getBeanClass() + "();");
		ln("        ktvalue.setKey(key);");
		ln("        ktvalue.setLabel(label);");
		ln("        ktvalue.persist();");
		ln("        return ktvalue;");
		ln("    }");

		ln();
		ln("    public static synchronized", bean.getBeanClass(), " createWithUuidKey(String label) {");
		ln("        return create(" + Uuid.class.getName() + ".create(), label);");
		ln("    }");

		ln();
		ln("    public static synchronized", bean.getBeanClass(), " createWithUuidKey() {");
		ln("        return create(" + Uuid.class.getName() + ".create(), \"#\"+listAll().size());");
		ln("    }");
	}

	private void writeListAll() {
		if (bean.isSingleton()) {
			ln();
			ln("    public static", bean.getName(), "get() {");
			ln("        List<" + bean.getName() + "> ret = new " + AllByTypeQuery.class.getName() + "("
					+ bean.getName() + ".class).list();");
			ln("        if (ret.isEmpty()) return null;");
			ln("        return ret.get(0);");
			ln("    }");

			ln();
			ln("    protected", bean.getName(), "createSingleton() {");
			ln("        return new", bean.getName() + "();");
			ln("    }");
		}

		if (!bean.isSingleton()) {
			ln();
			ln("    public static List<" + bean.getName() + "> listAll() {");
			ln("        return new " + AllByTypeQuery.class.getName() + "(" + bean.getName() + ".class).list();");
			ln("    }");

			ln();
			ln("    public static", bean.getName(), "getById(String id) {");
			ln("        return (" + bean.getName() + ") " + Transaction.class.getName() + ".get().get(id);");
			ln("    }");
		}
	}

	private void writeQueryBaseclass() {
		ln();
		ln("    public abstract static class A" + bean.getName() + "Query extends " + AEntityQuery.class.getName()
				+ "<" + bean.getName() + "> {");
		ln("        public Class<" + bean.getName() + "> getType() {");
		ln("            return " + bean.getName() + ".class;");
		ln("        }");
		ln("    }");
	}

	private void writePredicates() {
		String queryName = "A" + bean.getName() + "Query";
		for (PredicateModel p : bean.getPredicates()) {
			ln();
			ln("    public abstract boolean is" + Str.uppercaseFirstLetter(p.getName()) + "();");

			ln();
			ln("    public static List<" + bean.getBeanClass() + "> listByIs" + Str.uppercaseFirstLetter(p.getName())
					+ "() {");
			ln("        return new " + queryName + "() {");
			ln("            @Override");
			ln("            public boolean matches(" + bean.getName() + " entity) {");
			ln("                return entity.is" + Str.uppercaseFirstLetter(p.getName()) + "();");
			ln("            }");
			ln("        }.list();");
			ln("    }");
		}
	}

	private void writeGetPassengers() {
		// ln();
		// annotationOverride();
		// String setClass = "Set<" + ilarkesto.core.persistance.AEntity.class.getName() + ">";
		// ln("    public " + setClass + " getPassengers() {");
		// ln("        " + setClass + " ret = new HashSet<" +
		// ilarkesto.core.persistance.AEntity.class.getName() + ">();");
		// ln("        ret.addAll(super.getPassengers());");
		// for (PropertyModel p : bean.getSlaveProperties()) {
		// if (p.isCollection()) {
		// ln("        ret.addAll(get" + Str.uppercaseFirstLetter(p.getName()) + "());");
		// }
		// if (!p.isCollection()) {
		// ln("        ret.add(get" + Str.uppercaseFirstLetter(p.getName()) + "());");
		// }
		// }
		// ln("        return ret;");
		// ln("    }");
	}

	private void writeGetByListBy() {
		String queryName = "A" + bean.getName() + "Query";

		for (PropertyModel p : bean.getProperties()) {
			ln();
			if (p.isUnique()) {
				String byType = p.getType();
				if (p.isCollection()) byType = p.getContentType();
				ln("    public static", bean.getName(), "getBy" + Str.uppercaseFirstLetter(p.getNameSingular())
						+ "(final " + byType + " " + p.getName() + ") {");
				ln("        return (" + bean.getName() + ") " + Transaction.class.getName() + ".get().get(new "
						+ queryName + "() {");
				ln("            @Override");
				ln("            public boolean matches(" + bean.getName() + " entity) {");
				if (p.isCollection()) {
					ln("                return entity.contains" + Str.uppercaseFirstLetter(p.getNameSingular()) + "("
							+ p.getName() + ");");
				} else {
					ln("                return entity.is" + Str.uppercaseFirstLetter(p.getName()) + "(" + p.getName()
							+ ");");
				}
				ln("            }");
				ln("        });");
				ln("    }");

			}
			if (!p.isUnique()) {
				ln("    public static List<",
					bean.getName() + ">",
					"listBy" + Str.uppercaseFirstLetter(p.getNameSingular()) + "(final " + p.getContentType() + " "
							+ p.getNameSingular() + ") {");
				ln("        return new " + queryName + "() {");
				ln("            @Override");
				ln("            public boolean matches(" + bean.getName() + " entity) {");

				if (p.isCollection()) {
					ln("                return entity.contains" + Str.uppercaseFirstLetter(p.getNameSingular()) + "("
							+ p.getNameSingular() + ");");
				}

				if (!p.isCollection()) {
					ln("                return entity.is" + Str.uppercaseFirstLetter(p.getName()) + "(" + p.getName()
							+ ");");
				}
				ln("            }");
				ln("        }.list();");
				ln("    }");
			}

		}
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
			Set<String> refDaos = new LinkedHashSet<String>();
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
