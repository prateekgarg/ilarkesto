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
package ilarkesto.persistence;

import ilarkesto.core.base.Str;
import ilarkesto.core.persistance.ATransaction;
import ilarkesto.core.persistance.ATransactionManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public abstract class AThreadlocalTransactionManager<T extends ATransaction> extends ATransactionManager<T> {

	private ThreadLocal<T> tlTransaction = new ThreadLocal<T>();
	private Collection<T> transactions = Collections.synchronizedList(new ArrayList<T>());

	protected abstract T newInstance(String name);

	@Override
	public T createTransaction(String name) {
		T current = tlTransaction.get();
		if (current != null) {
			IllegalStateException ex = new IllegalStateException("Current thread alredy with transaction: " + current);
			// if (Sys.isDevelopmentMode()) {
			// throw ex;
			// } else {
			log.debug("Potential bug:", ex);
			// }
			return current;
		}

		current = newInstance(name + "/" + Str.format(Thread.currentThread()));
		transactions.add(current);
		tlTransaction.set(current);
		// log.debug("Transaction created:", current);

		return getTransaction();
	}

	@Override
	public final T getTransaction() {
		T current = tlTransaction.get();
		if (current == null) {
			current = newInstance(Str.format(Thread.currentThread()));
			transactions.add(current);
			tlTransaction.set(current);
			log.debug("Transaction created:", current);
		}
		return current;
	}

	@Override
	protected final void onTransactionFinished(T transaction) {
		T current = tlTransaction.get();
		if (current == null) {
			if (transaction == null) return;
			throw new IllegalStateException("Current transaction == null. Finished: " + transaction);
		}
		if (current != transaction)
			throw new IllegalStateException("Transaction is not current transaction: " + transaction);
		tlTransaction.set(null);
		transactions.remove(transaction);
	}

	@Override
	public final boolean isTransactionWithChangesOpen() {
		for (T transaction : new ArrayList<T>(transactions)) {
			if (!transaction.isEmpty()) return true;
		}
		return false;
	}

}
