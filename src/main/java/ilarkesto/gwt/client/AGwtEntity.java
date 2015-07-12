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
package ilarkesto.gwt.client;

import ilarkesto.core.persistance.ABaseEntity;
import ilarkesto.core.persistance.TransferableEntity;
import ilarkesto.core.time.Tm;
import ilarkesto.gwt.client.editor.AEditorModel;
import ilarkesto.gwt.client.undo.AUndoOperation;

import java.util.Map;

/**
 * Base class for entities.
 */
public abstract class AGwtEntity extends ABaseEntity implements TransferableEntity {

	private boolean inCreation;
	private transient Long localModificationTime;

	public abstract String getEntityType();

	protected abstract AGwtDao getDao();

	public AGwtEntity() {
		setId(getDao().getNewEntityId());
		inCreation = true;
		updateLastModified();
	}

	public AGwtEntity(Map data) {
		setId((String) data.get("id"));
		updateLastModified();
	}

	@Override
	public boolean isPersisted() {
		if (inCreation) return false;
		throw new RuntimeException(getClass().getName() + ".isPersisted() called");
	}

	@Override
	protected void onAfterUpdateLastModified() {
		updateLocalModificationTime();
		super.onAfterUpdateLastModified();
	}

	public void updateLocalModificationTime() {
		localModificationTime = Tm.getCurrentTimeMillis();
	}

	public final Long getLocalModificationTime() {
		return localModificationTime;
	}

	void setCreated() {
		this.inCreation = false;
	}

	protected final void propertyChanged(String property, String value) {
		if (inCreation) return;
		getDao().entityPropertyChanged(this, property, value);
		updateLastModified();
	}

	public boolean matchesKey(String key) {
		return false;
	}

	@Override
	public String toString() {
		return getId();
	}

	// --- helper ---

	protected static boolean matchesKey(Object object, String key) {
		if (object == null) return false;
		return object.toString().toLowerCase().indexOf(key) >= 0;
	}

	protected void addUndo(AEditorModel editorModel, Object oldValue) {
		Gwt.getUndoManager().add(new EditorModelUndo(editorModel, oldValue));
	}

	protected class EditorModelUndo extends AUndoOperation {

		private AEditorModel editorModel;
		private Object oldValue;

		public EditorModelUndo(AEditorModel editorModel, Object oldValue) {
			super();
			this.editorModel = editorModel;
			this.oldValue = oldValue;
		}

		@Override
		public String getLabel() {
			return "Undo Change on " + AGwtEntity.this.toString();
		}

		@Override
		protected void onUndo() {
			editorModel.setValue(oldValue);
		}

	}

}
