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

public abstract class ASingletonTransactionManager<T extends ATransaction> extends ATransactionManager<T> {

	private final Log log = Log.get(getClass());

	private T transaction;

	protected abstract T newTransaction();

	@Override
	public final T createWriteTransaction(String name) {
		return getCurrentTransaction();
	}

	@Override
	public final T getCurrentTransaction() {
		if (transaction == null) {
			transaction = newTransaction();
		}
		return transaction;
	}

	@Override
	public final void onTransactionFinished(T transaction) {
		if (this.transaction == transaction) this.transaction = null;
	}

	@Override
	public final boolean isTransactionWithChangesOpen() {
		if (transaction == null) return false;
		return !transaction.isEmpty();
	}

}
