/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package ilarkesto.core.base;

public abstract class Lazy<O> {

	private O object;

	protected abstract O create();

	public synchronized final O get() {
		if (object == null) {
			try {
				object = create();
			} catch (Exception ex) {
				throw new RuntimeException("Creating lazy instance failed.", ex);
			}
		}
		return object;
	}

	@Override
	public String toString() {
		return String.valueOf(object);
	}

	public final void release() {
		object = null;
	}

}
