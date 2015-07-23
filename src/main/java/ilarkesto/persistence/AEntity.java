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

import ilarkesto.auth.AuthUser;
import ilarkesto.base.Iconized;
import ilarkesto.base.OverrideExpectedException;
import ilarkesto.core.persistance.ABaseEntity;
import ilarkesto.core.persistance.ATransaction;
import ilarkesto.core.persistance.Persistence;
import ilarkesto.core.persistance.TransferableEntity;
import ilarkesto.core.search.SearchText;
import ilarkesto.core.search.Searchable;
import ilarkesto.core.time.DateAndTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AEntity extends ABaseEntity implements Datob, TransferableEntity, Iconized, Searchable {

	private static DaoService daoService;

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

	// --- ---

	public String getDeleteVeto() {
		return null;
	}

	@Override
	protected final void onAfterPersist() {
		super.onAfterPersist();
		daoService.fireEntitySaved(this);
	}

	@Override
	protected void onAfterDelete() {
		getDaoService().fireEntityDeleted(this);
	}

	public static AEntity getById(String entityId) {
		return daoService.getEntityById(entityId);
	}

	public static List<AEntity> getByIds(Collection<String> ids) {
		return daoService.getByIdsAsList(ids);
	}

	public static Set<AEntity> getByIdsAsSet(Collection<String> ids) {
		return daoService.getByIdsAsSet(ids);
	}

	protected final ADao getManager() {
		return getDao();
	}

	@Override
	public String getIcon() {
		return getDao().getIcon();
	}

	@Override
	public final Long getModificationTime() {
		Long modificationTime = super.getModificationTime();
		if (modificationTime == null && lastModified != null) modificationTime = lastModified.toMillis();
		return modificationTime;
	}

	public DateAndTime getLastModified() {
		return new DateAndTime(getModificationTime());
	}

	public final AuthUser getLastEditor() {
		if (this.lastEditorId == null) return null;
		return (AuthUser) AEntity.getById(this.lastEditorId);
	}

	public final void setLastEditor(AuthUser lastEditor) {
		if (isLastEditor(lastEditor)) return;
		this.lastEditorId = lastEditor == null ? null : lastEditor.getId();
		fireModified("lastEditor", Persistence.propertyAsString(lastEditorId));
	}

	public final boolean isLastEditor(AuthUser user) {
		if (this.lastEditorId == null && user == null) return true;
		return user != null && user.getId().equals(this.lastEditorId);
	}

	public final boolean isLastEditorSet() {
		return lastEditorId != null;
	}

	protected void repairDeadReferences(String entityId) {}

	@Override
	public boolean matches(SearchText searchText) {
		return false;
	}

	public static boolean exists(String id) {
		return ATransaction.get().containsWithId(id);
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

		@Override
		public boolean isPersisted() {
			return AEntity.this.isPersisted();
		}

	}

}
