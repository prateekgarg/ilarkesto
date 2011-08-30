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
package ilarkesto.async.fs;

import ilarkesto.async.Callback;
import ilarkesto.core.logging.Log;
import ilarkesto.testng.ATest;

import java.io.File;
import java.util.List;

import org.testng.annotations.Test;

public class ListFilesTest extends ATest {

	@Test
	public void simple() {
		new ListFiles("test-input/ListFiles", new Callback<List<File>>() {

			@Override
			public void onSuccess(List<File> result) {
				Log.DEBUG(result);
			}

			@Override
			public void onError(Throwable error) {
				fail("listing files failed", error);
			}
		}).setIncludeDirs(true).setRecurse(true).start();
	}
}
