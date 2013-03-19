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
package ilarkesto.law;

import ilarkesto.json.AJsonWrapper;
import ilarkesto.json.JsonObject;

public class NormRef extends AJsonWrapper {

	public NormRef(JsonObject json) {
		super(json);
	}

	public NormRef(String bookCode, String code) {
		putMandatory("bookCode", bookCode);
		putMandatory("code", code);
	}

	public String getCode() {
		return getMandatoryString("code");
	}

	public String getCodeNumber() {
		String code = getCode();
		int idx = code.lastIndexOf(' ');
		if (idx < 0) return code;
		return code.substring(idx + 1);
	}

	public boolean isCodeNumber(String codeNumber) {
		if (codeNumber == null) return false;
		return codeNumber.equals(getCodeNumber());
	}

	public String getBookCode() {
		return getMandatoryString("bookCode");
	}

	public String getBookCodeAndCode() {
		return getBookCode() + ", " + getCode();
	}

	@Override
	public String toString() {
		return getCodeAndBookCode();
	}

	private String getCodeAndBookCode() {
		return getCode() + " " + getBookCode();
	}

	@Override
	public boolean equals(Object obj) {
		return checkEquals(obj, "bookCode", "code");
	}

	@Override
	public int hashCode() {
		return hashCode("bookCode", "code");
	}

}
