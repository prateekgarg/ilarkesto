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
import ilarkesto.base.Utl;
import ilarkesto.concurrent.TaskManager;
import ilarkesto.core.persistance.EntitiesBackend;
import ilarkesto.core.persistance.InMemoryEntitiesBackend;
import ilarkesto.di.app.AApplication;
import ilarkesto.di.app.ApplicationStarter;
import ilarkesto.io.AFileChangeWatchTask;
import ilarkesto.io.IO;

import java.io.File;
import java.io.IOException;

public class EnhavoApplication extends AApplication {

	public static void main(String[] args) {
		ApplicationStarter.startApplication(EnhavoApplication.class, args);
	}

	private CmsContext cmsContext;
	private boolean watch;

	@Override
	protected void onStart() {
		CommandLineArgs cla = new CommandLineArgs(getArguments());
		String path = cla.popParameter();
		boolean loop = cla.popFlag("loop");
		watch = cla.popFlag("watch");

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

		cmsContext = new CmsContext(dir, null);

		if (loop) {
			loop(cmsContext);
			return;
		}

		cmsContext.build();
	}

	@Override
	protected boolean isPreventProcessEnd() {
		if (watch) return true;
		return super.isPreventProcessEnd();
	}

	@Override
	protected void scheduleTasks(TaskManager tm) {
		if (watch) tm.start(new WatchTask(cmsContext));
	}

	@Override
	protected void onShutdown() {}

	private void exitWithError(String message) {
		System.out.println(message);
		System.exit(1);
	}

	private static void loop(CmsContext cmsContext) {
		while (true) {
			cmsContext.build();
			Utl.sleep(5000);
		}
	}

	static class WatchTask extends AFileChangeWatchTask {

		private CmsContext cms;

		public WatchTask(CmsContext cms) {
			super(cms.getInputDir(), 300, 3000);
			this.cms = cms;
		}

		@Override
		protected void onChange() {
			cms.build();
		}

	}

	@Override
	protected EntitiesBackend createEntitiesBackend() {
		return new InMemoryEntitiesBackend();
	}

}
