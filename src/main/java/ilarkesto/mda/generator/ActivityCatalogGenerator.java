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
package ilarkesto.mda.generator;

import ilarkesto.base.Comparators;
import ilarkesto.core.base.Str;
import ilarkesto.gwt.client.desktop.AActivity;
import ilarkesto.gwt.client.desktop.AActivityCatalog;
import ilarkesto.io.IO;
import ilarkesto.mda.legacy.generator.AClassGenerator;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ActivityCatalogGenerator extends AClassGenerator {

	private String packageName;
	private FileFilter fileFilter;

	public ActivityCatalogGenerator(String packageName) {
		super();
		this.packageName = packageName;
	}

	@Override
	protected void writeContent() {
		writeInit();
		writeGetActivityNames();
		writeIsActivityAvailable();
		writeInstantiateActivity();
	}

	private void writeInit() {
		ln();
		ln("    static {");
		ln("        " + AActivityCatalog.class.getName() + ".INSTANCE = new ActivityCatalog();");
		ln("    }");
	}

	private void writeGetActivityNames() {
		ln();
		s("    public " + List.class.getName() + "<String>", "ACTIVITY_NAMES = " + Arrays.class.getName() + ".asList(");
		List<File> activities = findActivities();
		boolean first = true;
		for (File file : activities) {
			if (first) {
				first = false;
			} else {
				s(", ");
			}
			String activityName = Str.removeSuffix(file.getName(), ".java");
			s("\"" + activityName + "\"");
		}
		ln(");");

		ln();
		annotationOverride();
		ln("    public " + List.class.getName() + "<String> getActivityNames() {");
		ln("        return ACTIVITY_NAMES;");
		ln("    }");
	}

	private void writeIsActivityAvailable() {
		ln();
		annotationOverride();
		ln("    public  boolean isActivityAvailable(String name) {");
		List<File> activities = findActivities();
		for (File file : activities) {
			String activityName = Str.removeSuffix(file.getName(), "Activity.java");
			ln("        if (name.equals(\"" + activityName + "\")) return true;");
		}
		ln("        return false;");
		ln("    }");
	}

	private void writeInstantiateActivity() {
		ln();
		annotationOverride();
		ln("    public", AActivity.class.getName(), "instantiateActivity(String name) {");
		List<File> activities = findActivities();
		for (File file : activities) {
			String activityName = Str.removeSuffix(file.getName(), "Activity.java");
			String packageName = file.getParent();
			packageName = packageName.replace('/', '.').replace('\\', '.');
			packageName = Str.removePrefix(packageName, "src.");
			packageName = Str.removePrefix(packageName, "main.java.");
			String activityClass = packageName + "." + activityName + "Activity";
			ln("        if (name.equals(\"" + activityName + "\")) return new " + activityClass + "();");
		}
		ln("        throw new IllegalArgumentException(\"Activity does not exist: \" + name);");
		ln("    }");
	}

	private List<File> findActivities() {
		List<File> activities = IO.findFiles(new File("src"), new FileFilter() {

			@Override
			public boolean accept(File file) {
				String fileName = file.getName();
				if (file.getName().equals("AActivity.java")) return false;
				if (file.getParentFile().getName().equals("base") && fileName.startsWith("A")) return false;

				if (fileFilter != null && !fileFilter.accept(file)) return false;
				return file.getName().endsWith("Activity.java");
			}
		});
		Collections.sort(activities, Comparators.filesByNameIgnoreCase);
		return activities;
	}

	public ActivityCatalogGenerator setFileFilter(FileFilter filter) {
		this.fileFilter = filter;
		return this;
	}

	@Override
	protected String getName() {
		return "ActivityCatalog";
	}

	@Override
	protected String getSuperclass() {
		return AActivityCatalog.class.getName();
	}

	@Override
	protected String getPackage() {
		return packageName;
	}

	@Override
	protected boolean isInterface() {
		return false;
	}

	@Override
	protected boolean isAbstract() {
		return false;
	}

	@Override
	protected boolean isOverwrite() {
		return true;
	}

}
