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
package ilarkesto.auth;

import ilarkesto.persistence.AEntity;

import java.util.Map;

public class Auth {

	public static boolean isVisible(Object entity, AuthUser user) {
		if (entity instanceof ViewProtected) return ((ViewProtected) entity).isVisibleFor(user);
		if (entity instanceof Ownable) return ((Ownable) entity).isOwner(user);
		return true;
	}

	public static boolean isEditable(Object entity, AuthUser user) {
		if (entity instanceof EditProtected) return ((EditProtected) entity).isEditableBy(user);
		return isVisible(entity, user);
	}

	public static boolean isDeletable(Object entity, AuthUser user) {
		if (entity instanceof DeleteProtected) return ((DeleteProtected) entity).isDeletableBy(user);
		return isEditable(entity, user);
	}

	private Auth() {}

	public static boolean isEntityEditable(AuthUser user, AEntity entity, Map<String, String> properties) {
		return isEditable(entity, user);
	}

	public static boolean isEntityDeletable(AuthUser user, AEntity entity) {
		return isDeletable(entity, user);
	}

}
