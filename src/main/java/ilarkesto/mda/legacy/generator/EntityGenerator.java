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
import ilarkesto.core.localization.GermanComparator;
import ilarkesto.core.logging.Log;
import ilarkesto.core.persistance.AEntityBackReferenceHelper;
import ilarkesto.core.persistance.AEntityQuery;
import ilarkesto.core.persistance.AEntitySetBackReferenceHelper;
import ilarkesto.core.persistance.AllByTypeQuery;
import ilarkesto.core.persistance.EditableKeytableValue;
import ilarkesto.core.persistance.EntityDoesNotExistException;
import ilarkesto.core.persistance.KeytableValue;
import ilarkesto.core.persistance.Transaction;
import ilarkesto.mda.legacy.model.BackReferenceModel;
import ilarkesto.mda.legacy.model.EntityModel;
import ilarkesto.mda.legacy.model.PredicateModel;
import ilarkesto.mda.legacy.model.PropertyModel;
import ilarkesto.mda.legacy.model.ReferencePropertyModel;
import ilarkesto.mda.legacy.model.ReferenceSetPropertyModel;
import ilarkesto.persistence.ADatob;
import ilarkesto.persistence.AEntity;
import ilarkesto.search.Searchable;

import java.util.LinkedHashSet;
import java.util.List;
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
					ln("            fireModified(\"" + p.getName() + "\", datob.toString());");
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
			if (!bean.isAbstract()) {
				writeListAll();
				writeToString();
				writeGetByListBy();
			}
			writeQueryBaseclass();
			writePredicates();
			writeCollectPassengers();
			writeGetReferencedEntities();
			writeOnAfterPersist();
		}

		ln();
		ln("    @Override");
		ln("    public void storeProperties(Map<String, String> properties) {");
		ln("        super.storeProperties(properties);");
		for (PropertyModel p : bean.getProperties()) {
			String propertyVar;
			if (p.isCollection()) {
				propertyVar = p.isReference() ? p.getName() + "Ids" : p.getName();
			} else {
				propertyVar = p.isReference() ? p.getName() + "Id" : p.getName();
			}
			ln("        properties.put(\"" + propertyVar + "\", " + persistenceUtil + ".propertyAsString(this."
					+ propertyVar + "));");
		}
		ln("    }");

		if (!bean.isAbstract()) {
			ln();
			ln("    public int compareTo(" + bean.getName() + " other) {");
			ln("        return " + GermanComparator.class.getName()
					+ ".INSTANCE.compare(toString(), other.toString());");
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

	private void writeOnAfterPersist() {
		ln();
		annotationOverride();
		ln("    protected void onAfterPersist() {");
		ln("        super.onAfterPersist();");
		if (!bean.isAbstract()) {
			for (PropertyModel p : bean.getPropertiesAndSuperbeanProperties()) {
				if (!p.isReference()) continue;
				String suffix = p.isCollection() ? "s" : "";
				ln("        " + p.getName() + "BackReferencesCache.clear(get" + Str.uppercaseFirstLetter(p.getName())
						+ "Id" + suffix + "());");
			}
		}
		ln("    }");
	}

	private void writeGetReferencedEntities() {
		ln();
		annotationOverride();
		String setType = "Set<" + ilarkesto.core.persistance.AEntity.class.getName() + ">";
		ln("    public", setType, "getReferencedEntities() {");
		ln("        " + setType + " ret = super.getReferencedEntities();");

		Set<PropertyModel> properties = bean.getProperties();
		if (!properties.isEmpty()) comment("references");
		for (PropertyModel p : properties) {
			String pNameUpper = Str.uppercaseFirstLetter(p.getName());
			if (p instanceof ReferencePropertyModel) {
				ln("        try { Utl.addIfNotNull(ret, get" + pNameUpper
						+ "()); } catch(EntityDoesNotExistException ex) {}");
			} else if (p instanceof ReferenceSetPropertyModel) {
				ln("        for (String id : " + p.getName() + "Ids) {");
				ln("            try { ret.add(AEntity.getById(id)); } catch(EntityDoesNotExistException ex) {}");
				ln("        }");
			}
		}

		List<BackReferenceModel> backReferences = bean.getBackReferences();
		if (!backReferences.isEmpty()) comment("back references");
		for (BackReferenceModel br : backReferences) {
			String brNameUpper = Str.uppercaseFirstLetter(br.getName());
			PropertyModel p = br.getReference();
			if (p.isUnique()) {
				ln("        Utl.addIfNotNull(ret, get" + brNameUpper + "());");
			} else {
				ln("        ret.addAll(get" + brNameUpper + "s());");
			}
		}

		ln("        return ret;");
		ln("    }");
	}

	@Override
	protected void writeEnsureIntegrityContent() {
		super.writeEnsureIntegrityContent();

		if (bean.isSingleton()) {
			ln("        if (listAll().size() > 1) throw new IllegalStateException(\"Multiple singleton instances: "
					+ bean.getName() + "\");");
		}

		List<BackReferenceModel> backReferences = bean.getBackReferences();
		for (BackReferenceModel br : backReferences) {
			PropertyModel reference = br.getReference();
			if (isLegacyBean(bean) && reference.getBean().isAbstract()) continue;
			if (reference.isUnique()) {
				ln("        " + br.getReference().getBean().getBeanClass(), br.getName(), "=",
					"get" + Str.uppercaseFirstLetter(br.getName()) + "();");
			} else {
				ln("        Collection<" + reference.getBean().getBeanClass() + ">", br.getName(), "=",
					"get" + Str.uppercaseFirstLetter(br.getName()) + "s();");
			}
		}

		if (!bean.isSelfcontained()) {
			Set<ReferencePropertyModel> masterReferences = bean.getMasterReferences();
			if (masterReferences.isEmpty()) {
				if (!backReferences.isEmpty()) {
					boolean first = true;
					s("        if (");
					for (BackReferenceModel br : backReferences) {
						if (first) {
							first = false;
						} else {
							s(" && ");
						}
						s(br.getName(), "== null");
					}
					ln(") {");
					ln("            log.info(\"Deleting unreferenced entity: \" + getId());");
					ln("            delete();");
					ln("        }");
				}
			}
		}

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
			ln("        Set<" + bean.getName() + "> ret = new " + AllByTypeQuery.class.getName() + "(" + bean.getName()
					+ ".class).list();");
			ln("        if (ret.isEmpty()) return null;");
			ln("        return ret.iterator().next();");
			ln("    }");

			ln();
			ln("    protected", bean.getName(), "createSingleton() {");
			ln("        return new", bean.getName() + "();");
			ln("    }");
		}

		ln();
		ln("    public static Set<" + bean.getName() + "> listAll() {");
		ln("        return new " + AllByTypeQuery.class.getName() + "(" + bean.getName() + ".class).list();");
		ln("    }");

		ln();
		ln("    public static", bean.getName(), "getById(String id) {");
		ln("        return (" + bean.getName() + ") " + Transaction.class.getName() + ".get().get(id);");
		ln("    }");
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
			ln("    public static Set<" + bean.getBeanClass() + "> listByIs" + Str.uppercaseFirstLetter(p.getName())
					+ "() {");
			ln("        return new " + queryName + "() {");
			ln("            @Override");
			ln("            public boolean test(" + bean.getName() + " entity) {");
			ln("                return entity.is" + Str.uppercaseFirstLetter(p.getName()) + "();");
			ln("            }");
			ln("            @Override");
			ln("            public String toString() {");
			ln("                return \"byIs" + Str.uppercaseFirstLetter(p.getName()) + "\";");
			ln("            }");
			ln("        }.list();");
			ln("    }");
		}
	}

	private void writeCollectPassengers() {
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

		for (PropertyModel p : bean.getPropertiesAndSuperbeanProperties()) {
			ln();
			if (p.isUnique()) {

				if (p.isReference()) {
					ln("    private static transient",
						AEntityBackReferenceHelper.class.getName() + "<" + bean.getName() + ">", p.getName()
								+ "BackReferencesCache = new",
						AEntityBackReferenceHelper.class.getName() + "<" + bean.getName() + ">() {");
					annotationOverride();
					ln("        protected " + bean.getName() + " loadById(final String id) {");
					ln("        return new " + queryName + "() {");
					ln("            @Override");
					ln("            public boolean test(" + bean.getName() + " entity) {");

					if (p.isCollection()) {
						ln("                return entity.get" + Str.uppercaseFirstLetter(p.getName())
								+ "Ids().contains(id);");
					}

					if (!p.isCollection()) {
						ln("                return id.equals(entity.get" + Str.uppercaseFirstLetter(p.getName())
								+ "Id());");
					}
					ln("            }");
					ln("            }.getFirst();");
					ln("            }");
					ln("            @Override");
					ln("            public String toString() {");
					ln("                return \"by" + Str.uppercaseFirstLetter(p.getName()) + "\";");
					ln("            }");
					ln("    };");
					ln();
				}
				String byType = p.getType();
				if (p.isCollection()) byType = p.getContentType();
				ln("    public static", bean.getName(), "getBy" + Str.uppercaseFirstLetter(p.getNameSingular())
						+ "(final " + byType + " " + p.getName() + ") {");
				if (p.isReference()) {
					ln("        if (" + p.getName() + " == null ) return null;");
					ln("        return", p.getName() + "BackReferencesCache.getById(" + p.getName() + ".getId());");
				} else {
					ln("        return (" + bean.getName() + ") " + Transaction.class.getName()
							+ ".get().getFirst(new " + queryName + "() {");
					ln("            @Override");
					ln("            public boolean test(" + bean.getName() + " entity) {");
					if (p.isCollection()) {
						ln("                return entity.contains" + Str.uppercaseFirstLetter(p.getNameSingular())
								+ "(" + p.getName() + ");");
					} else {
						ln("                return entity.is" + Str.uppercaseFirstLetter(p.getName()) + "("
								+ p.getName() + ");");
					}
					ln("            }");
					ln("            @Override");
					ln("            public String toString() {");
					ln("                return \"by" + Str.uppercaseFirstLetter(p.getName()) + "\";");
					ln("            }");
					ln("        });");
				}
				ln("    }");

			}

			if (!p.isUnique()) {
				if (p.isReference()) {
					ln("    private static transient",
						AEntitySetBackReferenceHelper.class.getName() + "<" + bean.getName() + ">", p.getName()
								+ "BackReferencesCache = new", AEntitySetBackReferenceHelper.class.getName() + "<"
								+ bean.getName() + ">() {");
					annotationOverride();
					ln("        protected Set<" + bean.getName() + "> loadById(final String id) {");
					ln("        return new " + queryName + "() {");
					ln("            @Override");
					ln("            public boolean test(" + bean.getName() + " entity) {");

					if (p.isCollection()) {
						ln("                return entity.get" + Str.uppercaseFirstLetter(p.getName())
								+ "Ids().contains(id);");
					}

					if (!p.isCollection()) {
						ln("                return id.equals(entity.get" + Str.uppercaseFirstLetter(p.getName())
								+ "Id());");
					}

					ln("            }");
					ln("            @Override");
					ln("            public String toString() {");
					ln("                return \"by" + Str.uppercaseFirstLetter(p.getName()) + "\";");
					ln("            }");
					ln("        }.list();");
					ln("        }");
					ln("    };");
					ln();
				}

				ln("    public static Set<",
					bean.getName() + ">",
					"listBy" + Str.uppercaseFirstLetter(p.getNameSingular()) + "(final " + p.getContentType() + " "
							+ p.getNameSingular() + ") {");

				if (p.isReference()) {
					ln("        if (" + p.getNameSingular(), "== null) return new HashSet<" + bean.getName() + ">();");
					ln("        return", p.getName() + "BackReferencesCache.getById(" + p.getNameSingular()
							+ ".getId());");
				}

				if (!p.isReference()) {
					ln("        return new " + queryName + "() {");
					ln("            @Override");
					ln("            public boolean test(" + bean.getName() + " entity) {");

					if (p.isCollection()) {
						ln("                return entity.contains" + Str.uppercaseFirstLetter(p.getNameSingular())
								+ "(" + p.getNameSingular() + ");");
					}

					if (!p.isCollection()) {
						ln("                return entity.is" + Str.uppercaseFirstLetter(p.getName()) + "("
								+ p.getName() + ");");
					}

					ln("            }");
					ln("            @Override");
					ln("            public String toString() {");
					ln("                return \"by" + Str.uppercaseFirstLetter(p.getName()) + "\";");
					ln("            }");
					ln("        }.list();");
				}
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
				ln("    public final Set<" + refEntity.getBeanClass() + "> get"
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
	protected Set<String> getImports() {
		Set<String> ret = super.getImports();
		if (isLegacyBean(bean)) {
			ret.add(AEntity.class.getName());
		} else {
			ret.add(ilarkesto.core.persistance.AEntity.class.getName());
		}
		ret.add(EntityDoesNotExistException.class.getName());
		return ret;
	}

	@Override
	protected boolean isCopyConstructorEnabled() {
		return false;
	}

}
