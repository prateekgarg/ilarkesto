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
package ilarkesto.runtime;

import ilarkesto.base.Sys;
import ilarkesto.io.IO;

public class AutoProxy {

	public static void update() {
		if (!Sys.isDevelopmentMode()) return;
		if (isHis()) {
			Sys.setHttpProxy("83.246.65.146", 80);
			return;
		}
	}

	private static boolean isHis() {
		try {
			return IO.downloadUrlToString("http://xqisdev.his.de").contains("<title>qisdev</title>");
		} catch (Throwable ex) {
			return false;
		}
	}

}
