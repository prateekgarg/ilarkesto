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
package ilarkesto.tools.enhavo;

import ilarkesto.base.CommandLineArgs;
import ilarkesto.io.IO;

import java.io.File;
import java.io.IOException;

public class Enhavo {

	public static void main(String[] args) throws InterruptedException {
		CommandLineArgs cla = new CommandLineArgs(args);
		boolean loop = cla.popFlag("loop");
		String path = cla.popParameter();
		if (path == null) {
			exitWithError("Missing <cms-path> parameter");
			return;
		}
		if (cla.containsAny()) {
			exitWithError("Too many arguments");
			return;
		}

		File dir;
		try {
			dir = new File(path).getCanonicalFile().getAbsoluteFile();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		if (!dir.exists()) {
			System.out.println("Creating new CMS: " + dir.getPath());
			IO.createDirectory(dir);
		}

		CmsContext cmsContext = new CmsContext(dir, null);

		if (loop) {
			loop(cmsContext);
			return;
		}

		cmsContext.build();
	}

	private static void exitWithError(String message) {
		System.out.println(message);
		System.exit(1);
	}

	private static void loop(CmsContext cmsContext) throws InterruptedException {
		while (true) {
			cmsContext.build();
			Thread.sleep(5000);
		}
	}

}
