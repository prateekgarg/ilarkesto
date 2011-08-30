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
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.async;

public class Async {

	private static AsyncEngine asyncEngine;

	public static void start(Job job) {
		getAsyncEngine().start(job);
	}

	public static AsyncEngine getAsyncEngine() {
		if (asyncEngine == null) asyncEngine = new ExecutorAsyncEngine();
		return asyncEngine;
	}

	public static void setAsyncEngine(AsyncEngine asyncEngine) {
		Async.asyncEngine = asyncEngine;
	}

}
