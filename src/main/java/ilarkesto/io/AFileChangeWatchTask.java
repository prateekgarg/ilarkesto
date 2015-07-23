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
package ilarkesto.io;

import ilarkesto.concurrent.ALoopTask;
import ilarkesto.core.base.RuntimeTracker;

import java.io.File;
import java.util.Map;

public abstract class AFileChangeWatchTask extends ALoopTask {

	private File root;
	private long minSleep;
	private long maxSleep;
	private long sleepIncrement = 100;

	private Map<String, Long> modificationTimesByPath;
	private long sleep;
	private long warningRuntime = 2000;

	protected abstract void onChange();

	public AFileChangeWatchTask(File root, long minSleep, long maxSleep) {
		super();
		this.root = root;
		this.minSleep = minSleep;
		this.maxSleep = maxSleep;

		sleep = minSleep;
	}

	protected void onFirstChange() {
		onChange();
	}

	@Override
	protected void beforeLoop() throws InterruptedException {
		modificationTimesByPath = IO.getModificationTimes(root);
		onFirstChange();
	}

	@Override
	protected void iteration() throws InterruptedException {
		RuntimeTracker rt = new RuntimeTracker();
		Map<String, Long> newModificationTimes = IO.getModificationTimes(root);
		if (rt.getRuntime() >= warningRuntime)
			log.warn("Checking modification times took", rt.getRuntimeFormated(), "->", root.getAbsolutePath());

		if (newModificationTimes.equals(modificationTimesByPath)) {
			sleep = Math.min(maxSleep, sleep + sleepIncrement);
			return;
		}

		modificationTimesByPath = newModificationTimes;
		sleep = minSleep;
		onChange();
	}

	@Override
	protected long getSleepTimeBetweenIterations() {
		return sleep;
	}

}
