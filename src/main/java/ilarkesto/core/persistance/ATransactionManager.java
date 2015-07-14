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
package ilarkesto.core.persistance;

import ilarkesto.core.logging.Log;

public abstract class ATransactionManager<T extends ATransaction> {

	protected final Log log = Log.get(getClass());

	public abstract T getTransaction();

	public abstract T createTransaction(String name);

	protected abstract void onTransactionFinished(T transaction);

	public abstract boolean isTransactionWithChangesOpen();

	public final void transactionFinished(T transaction) {
		log.debug("Transaction finished:", transaction);
		onTransactionFinished(transaction);
	}

}
