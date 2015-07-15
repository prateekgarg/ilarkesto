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

	private ThreadLocal<T> threadlocal = new ThreadLocal<T>();
	private Collection<T> transactions = Collections.synchronizedList(new ArrayList<T>());

	protected abstract T newInstance(String name, boolean writable);

	@Override
	public synchronized T createWriteTransaction(String name) {
		T parent = null;

		T current = threadlocal.get();
		if (current != null) {

			if (current.isWritable()) {
				parent = current;
			} else {
				unbind(current);
			}

		}

		T newTransaction = createAndBind(name, true);
		newTransaction.setParentTransaction(parent);
		return newTransaction;
	}

	@Override
	public synchronized final T getCurrentTransaction() {
		T current = threadlocal.get();
		if (current != null) return current;
		return createAndBind("readOnly", false);
	}

	@Override
	protected synchronized final void onTransactionFinished(ATransaction transaction) {
		ATransaction current = threadlocal.get();
		if (current == null) throw new IllegalStateException("Current transaction == null. Finished: " + transaction);

		if (current != transaction)
			throw new IllegalStateException("Transaction is not current transaction: " + transaction);

		unbind(transaction);

		ATransaction parent = transaction.getParentTransaction();
		if (parent != null) threadlocal.set((T) parent);
	}

	private T createAndBind(String name, boolean writable) {
		T t = newInstance(name + "/" + Str.format(Thread.currentThread()), writable);
		transactions.add(t);
		threadlocal.set(t);
		return t;
	}

	private void unbind(ATransaction t) {
		threadlocal.set(null);
		transactions.remove(t);
	}

	@Override
	public synchronized final boolean isTransactionWithChangesOpen() {
		for (T transaction : new ArrayList<T>(transactions)) {
			if (!transaction.isEmpty()) return true;
		}
		return false;
	}

}
