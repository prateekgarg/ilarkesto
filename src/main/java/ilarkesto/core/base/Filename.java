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

public class Filename {

	private String filename;
	private String prefix;
	private String suffix;

	public Filename(String filename) {
		Args.assertNotBlank(filename, "filename");
		this.filename = filename;
	}

	public Filename(String prefix, String suffix) {
		Args.assertNotBlank(prefix, "prefix");
		this.prefix = prefix;
		this.suffix = suffix;

		filename = Str.isBlank(suffix) ? prefix : prefix + "." + suffix;
	}

	public String getSuffix() {
		if (suffix == null && prefix == null) parse();
		return suffix;
	}

	public String getPrefix() {
		if (suffix == null && prefix == null) parse();
		return prefix;
	}

	private void parse() {
		int idx = filename.lastIndexOf('.');
		if (idx < 1 || idx == filename.length() - 1) {
			prefix = filename;
			suffix = null;
		} else {
			prefix = filename.substring(0, idx);
			suffix = filename.substring(idx + 1);
		}
	}

	@Override
	public String toString() {
		return filename;
	}

	public boolean isImage() {
		if (suffix == null && prefix == null) parse();
		if (suffix == null) return false;
		String type = suffix.toLowerCase();
		if (type.equals("png")) return true;
		if (type.equals("jpg")) return true;
		if (type.equals("gif")) return true;
		if (type.equals("jpeg")) return true;
		return false;
	}

}
