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
import ilarkesto.base.Iconized;
import ilarkesto.base.Utl;
import ilarkesto.core.persistance.Persistence;
import ilarkesto.core.persistance.TransferBus;
import ilarkesto.core.persistance.TransferableEntity;
import ilarkesto.core.time.DateAndTime;
import ilarkesto.id.Identifiable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class AEntity extends ADatob implements Identifiable, Iconized, TransferableEntity {

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

	@Override
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

	@Override
	protected void fireModified(String field, String value) {
		super.fireModified(field, value);
	}

	@Override
	public void updateLastModified() {
		modificationTime = System.currentTimeMillis();
	}

	@Override
	public void ensureIntegrity() {
		super.ensureIntegrity();
	}

	@Override
	protected void storeProperties(Map<String, String> properties) {
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

}
