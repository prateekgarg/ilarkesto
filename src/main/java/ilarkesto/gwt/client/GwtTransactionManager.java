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
package ilarkesto.gwt.client;

import ilarkesto.core.logging.Log;
import ilarkesto.core.persistance.ATransactionManager;
import ilarkesto.core.persistance.Transaction;

public class GwtTransactionManager extends ATransactionManager<Transaction> {

	private static final Log log = Log.get(GwtTransactionManager.class);

	private Transaction transaction;

	@Override
	public Transaction createWriteTransaction(String name) {
		return getCurrentTransaction();
	}

	@Override
	public Transaction getCurrentTransaction() {
		if (transaction == null) {
			transaction = new Transaction("GWT", false, true, false);
			log.info("Transaction created:", transaction.getName());
		}
		return transaction;
	}

	@Override
	public void onTransactionFinished(Transaction transaction) {
		log.debug("Transaction finished:", transaction.getName());
		if (this.transaction == transaction) this.transaction = null;
	}

	@Override
	public boolean isTransactionWithChangesOpen() {
		if (transaction == null) return false;
		return !transaction.isEmpty();
	}

}
