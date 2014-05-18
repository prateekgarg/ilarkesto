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

import ilarkesto.core.logging.Log;
import ilarkesto.core.persistance.AEntity;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public abstract class AGwtApplication<D extends ADataTransferObject> implements EntryPoint {

	protected final Log log = Log.get(getClass());

	private static AGwtApplication singleton;
	protected int conversationNumber = -1;

	public abstract void handleServiceCallError(String serviceCall, List<ErrorWrapper> errors);

	protected abstract void handleUnexpectedError(Throwable ex);

	protected abstract AGwtDao getDao();

	public AGwtApplication() {
		if (singleton != null) throw new RuntimeException("GWT application already instantiated: " + singleton);
		singleton = this;
		Log.setLogRecordHandler(new GwtLogRecordHandler());
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {

			@Override
			public void onUncaughtException(Throwable ex) {
				handleUnexpectedError(ex);
			}
		});
	}

	final void serverDataReceived(D data) {
		if (data.conversationNumber != null) {
			log.info("conversatioNumber received:", data.conversationNumber);
			conversationNumber = data.conversationNumber;
		}
		if (data.containsDeletedEntities()) {
			Set<String> entityIds = data.getDeletedEntities();
			log.debug("entity deletions received:", entityIds);
			onEntityDeletionsReceived(entityIds);
		}
		if (data.containsEntities()) {
			Collection<Map<String, Serializable>> entities = data.getEntities();
			log.debug("entities received:", entities);
			onEntitiesReceived(entities);
		}
		if (data.isUserSet()) {
			String userId = data.getUserId();
			log.info("user-id received:", userId);
			onUserIdReceived(userId);
		}
		onServerDataReceived(data);
	}

	protected void onEntitiesReceived(Collection<Map<String, Serializable>> entities) {}

	protected void onEntityDeletionsReceived(Set<String> entityIds) {}

	protected void onUserIdReceived(String userId) {}

	protected void onServerDataReceived(D data) {}

	public final int getConversationNumber() {
		return conversationNumber;
	}

	public final void resetConversation() {
		conversationNumber = -1;
	}

	@Override
	public String toString() {
		return getClass().getName();
	}

	public static AGwtApplication get() {
		return singleton;
	}

	public abstract void sendChangesToServer(Collection<AEntity> modified, Collection<String> deleted,
			Map<String, Map<String, Object>> modifiedProperties);

}
