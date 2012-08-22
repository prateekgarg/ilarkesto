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
package ilarkesto.tools.deletebackups;

import ilarkesto.core.time.Date;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeleteBackups {

	public static void main(String[] args) {
		int ret = deleteBackups(args);
		System.exit(ret);
	}

	private static int deleteSomeBackupFiles(File[] files) {
		List<Backup> backups = filterBackups(files);
		return fail("not implemented yet");
	}

	private static List<Backup> filterBackups(File[] files) {
		List<Backup> backups = new ArrayList<DeleteBackups.Backup>();
		for (File file : files) {
			if (!file.isFile()) continue;
			Date date = extractDate(file.getName());
			if (date == null) continue;
			backups.add(new Backup(file, date));
		}
		Collections.sort(backups);
		return backups;
	}

	private static int deleteBackups(File dir) {
		if (!dir.exists()) return fail("Backup directory does not exist: " + dir.getAbsolutePath());
		return deleteSomeBackupFiles(dir.listFiles());
	}

	private static int deleteBackups(String[] args) {
		if (args.length != 1) return fail("Bad syntax: Exactly one argument (the backup directory path) required");
		return deleteBackups(new File(args[0]));
	}

	private static int fail(String message) {
		System.err.println("Deleting backups failed.\n-> " + message);
		return 1;
	}

	public static Date extractDate(String s) {
		Matcher matcher = Pattern.compile(".*(20\\d\\d-\\d\\d-\\d\\d).*").matcher(s);
		if (!matcher.matches()) return null;
		String date = matcher.group(1);
		if (date == null) return null;
		return new Date(date);
	}

	static class Backup implements Comparable<Backup> {

		private File file;
		private Date date;

		public Backup(File file, Date date) {
			super();
			this.file = file;
			this.date = date;
		}

		@Override
		public int compareTo(Backup o) {
			return o.date.compareTo(date);
		}

	}
}
