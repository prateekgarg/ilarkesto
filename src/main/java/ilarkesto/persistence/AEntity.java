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
package ilarkesto.persistence;

import ilarkesto.auth.AUser;
import ilarkesto.auth.AUserDao;
import ilarkesto.base.Iconized;
import ilarkesto.base.OverrideExpectedException;
import ilarkesto.base.Utl;
import ilarkesto.core.persistance.ABaseEntity;
import ilarkesto.core.persistance.Persistence;
import ilarkesto.core.persistance.TransferBus;
import ilarkesto.core.persistance.TransferableEntity;
import ilarkesto.core.search.SearchText;
import ilarkesto.core.search.Searchable;
import ilarkesto.core.time.DateAndTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class AEntity extends ABaseEntity implements Datob, TransferableEntity, Iconized, Searchable {

	private static DaoService daoService;

	private String id;
	private Long modificationTime;
	private String lastEditorId;

	@Deprecated
	private DateAndTime lastModified;

	public abstract ADao getDao();

	// --- dependencies ---

	public static DaoService getDaoService() {
		return daoService;
	}

	public final static void setDaoService(DaoService daoService) {
		AEntity.daoService = daoService;
	}

	protected static AUserDao userDao;

	public static void setUserDao(AUserDao userDao) {
		AEntity.userDao = userDao;
	}

	// --- ---

	public static AEntity getById(String entityId) {
		return daoService.getEntityById(entityId);
	}

	public static List<AEntity> getByIds(Collection<String> ids) {
		return daoService.getByIds(ids);
	}

	public static Set<AEntity> getByIdsAsSet(Collection<String> ids) {
		return daoService.getByIdsAsSet(ids);
	}

	protected boolean isDeleted() {
		return getDao().isDeleted(this);
	}

	protected final ADao getManager() {
		return getDao();
	}

	@Override
	public String getIcon() {
		return getDao().getIcon();
	}

	@Override
	public final String getId() {
		if (id == null) id = UUID.randomUUID().toString();
		return id;
	}

	final void setId(String id) {
		this.id = id;
	}

	@Override
	public final Long getModificationTime() {
		if (modificationTime == null && lastModified != null) modificationTime = lastModified.toMillis();
		return modificationTime;
	}

	public DateAndTime getLastModified() {
		return new DateAndTime(getModificationTime());
	}

	public final AUser getLastEditor() {
		if (this.lastEditorId == null) return null;
		return (AUser) userDao.getById(this.lastEditorId);
	}

	public final void setLastEditor(AUser lastEditor) {
		if (isLastEditor(lastEditor)) return;
		this.lastEditorId = lastEditor == null ? null : lastEditor.getId();
		fireModified("lastEditor", Persistence.propertyAsString(lastEditorId));
	}

	public final boolean isLastEditor(AUser user) {
		if (this.lastEditorId == null && user == null) return true;
		return user != null && user.getId().equals(this.lastEditorId);
	}

	public final boolean isLastEditorSet() {
		return lastEditorId != null;
	}

	protected void fireModified(String field, String value) {
		getDao().onDatobModified(this, field, value);
	}

	public void updateLastModified() {
		modificationTime = System.currentTimeMillis();
	}

	public void ensureIntegrity() {
		// super.ensureIntegrity();
	}

	protected void storeProperties(Map<String, String> properties) {
		// super.storeProperties(properties);
		properties.put("@type", getDao().getEntityName());
		properties.put("id", getId());
		properties.put("modificationTime", getModificationTime().toString());
	}

	@Override
	public <E extends TransferableEntity> void collectPassengers(TransferBus bus) {}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (!getClass().equals(o.getClass())) return false;
		return Utl.equals(getId(), ((AEntity) o).getId());
	}

	@Override
	public final int hashCode() {
		if (id == null) return 0;
		return id.hashCode();
	}

	public void updateProperties(Map<String, String> properties) {}

	protected void repairDeadReferences(String entityId) {}

	protected final void repairMissingMaster() {
		ADatobManager manager = getManager();
		if (manager == null) return;
		manager.onMissingMaster(this);
	}

	@Override
	public final HashMap<String, String> createPropertiesMap() {
		HashMap<String, String> properties = new HashMap<String, String>();
		storeProperties(properties);
		return properties;
	}

	@Override
	public boolean matches(SearchText searchText) {
		return false;
	}

	// --- helper from datob ---

	protected static void repairDeadReferencesOfValueObjects(Collection<? extends ADatob> valueObjects, String entityId) {
		for (ADatob vo : valueObjects)
			vo.repairDeadReferences(entityId);
	}

	protected final <S extends AStructure> Set<S> cloneValueObjects(Collection<S> strucktures, ADatobManager<S> manager) {
		Set<S> ret = new HashSet<S>();
		for (S s : strucktures) {
			ret.add((S) s.clone(manager));
		}
		return ret;
	}

	protected static Set<String> getIdsAsSet(Collection<? extends AEntity> entities) {
		Set<String> result = new HashSet<String>(entities.size());
		for (AEntity entity : entities)
			result.add(entity.getId());
		return result;
	}

	protected static List<String> getIdsAsList(Collection<? extends AEntity> entities) {
		List<String> result = new ArrayList<String>(entities.size());
		for (AEntity entity : entities)
			result.add(entity.getId());
		return result;
	}

	protected static boolean matchesKey(String s, String key) {
		if (s == null) return false;
		return s.toLowerCase().contains(key);
	}

	protected void repairDeadDatob(ADatob datob) {
		throw new OverrideExpectedException();
	}

	public class StructureManager<D extends ADatob> extends ADatobManager<D> {

		@Override
		public void onDatobModified(D datob, String field, String value) {
			fireModified(field, value);
		}

		@Override
		public void updateLastModified(D datob) {
			AEntity.this.updateLastModified();
		}

		@Override
		public void onMissingMaster(D datob) {
			repairDeadDatob(datob);
		}

		@Override
		public void ensureIntegrityOfStructures(Collection<D> structures) {
			for (ADatob structure : new ArrayList<ADatob>(structures)) {
				((AStructure) structure).setManager(this);
				structure.ensureIntegrity();
			}
		}

	}

}
