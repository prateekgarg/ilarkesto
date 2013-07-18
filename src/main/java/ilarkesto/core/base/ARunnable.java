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

import ilarkesto.core.logging.Log;

public abstract class ARunnable implements Runnable {

	protected Log log = Log.get(getClass());

	protected abstract void onRun() throws InterruptedException;

	@Override
	public final void run() {
		try {
			onRun();
		} catch (InterruptedException ex) {
			log.info("Thread interrupted:", Thread.currentThread());
			return;
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	// --- utils ---

	protected final void sleep(long millis) throws InterruptedException {
		Thread.sleep(millis);
	}

}
