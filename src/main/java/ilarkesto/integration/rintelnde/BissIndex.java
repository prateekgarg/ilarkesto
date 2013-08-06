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
package ilarkesto.integration.rintelnde;

import ilarkesto.json.AJsonWrapper;
import ilarkesto.json.JsonObject;

import java.util.List;

public class BissIndex extends AJsonWrapper {

	public BissIndex(JsonObject json) {
		super(json);
	}

	public BissIndex() {}

	public List<Lebenslage> getLebenslages() {
		return getWrapperArray("lebenslages", Lebenslage.class);
	}

	public void setLebenslages(List<Lebenslage> lebenslages) {
		putArray("lebenslages", lebenslages);
	}

	public static class Lebenslage extends AJsonWrapper {

		public Lebenslage(JsonObject json) {
			super(json);
		}

		public Lebenslage(String label, Integer id) {
			putMandatory("label", label);
			putMandatory("id", id);
		}

		public String getLabel() {
			return json.getString("label");
		}

		public Integer getId() {
			return json.getInteger("id");
		}

		public List<Anliegen> getAnliegens() {
			return getWrapperArray("anliegens", Anliegen.class);
		}

		public void setAnliegens(List<Anliegen> anliegens) {
			putArray("anliegens", anliegens);
		}

		@Override
		public String toString() {
			return getLabel();
		}

		public static class Anliegen extends AJsonWrapper implements RintelnDePage, Comparable<Anliegen> {

			public Anliegen(String label, Integer id) {
				putMandatory("label", label);
				putMandatory("id", id);
			}

			@Override
			public String getPagePath() {
				return RintelnDe.PAGE_BISS + "/anliegen/" + getId() + "_";
			}

			public boolean isFirstInLebenslage() {
				List<Anliegen> anliegens = getLebenslage().getAnliegens();
				if (anliegens.isEmpty()) return false;
				Anliegen first = anliegens.get(0);
				return first.getId().equals(getId()) && first.getLabel().equals(getLabel());
			}

			public Lebenslage getLebenslage() {
				return getParent(Lebenslage.class);
			}

			public Anliegen(JsonObject json) {
				super(json);
			}

			public String getLabel() {
				return json.getString("label");
			}

			public Integer getId() {
				return json.getInteger("id");
			}

			@Override
			public int compareTo(Anliegen o) {
				return getLabel().compareTo(o.getLabel());
			}

			@Override
			public String toString() {
				return getLabel();
			}

		}

	}

}
