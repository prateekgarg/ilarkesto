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
package ilarkesto.integration.kba.btkatowi;

import ilarkesto.json.AJsonWrapper;
import ilarkesto.json.JsonObject;

import java.math.BigDecimal;
import java.util.List;

public class Tatbestandskatalog extends AJsonWrapper {

	private static final BigDecimal HUNDRED = new BigDecimal(100);

	public Tatbestandskatalog(JsonObject json) {
		super(json);
	}

	public Tatbestandskatalog(List<Tatbestand> tatbestands) {
		json.addToArray("tatbestandGroups", new TatbestandGroup("Straßenverkehrs-Ordnung"));
		json.addToArray("tatbestandGroups", new TatbestandGroup("Fahrerlaubnis-Verordnung"));
		json.addToArray("tatbestandGroups", new TatbestandGroup("Straßenverkehrs-Zulassungsordnung"));
		json.addToArray("tatbestandGroups", new TatbestandGroup("Straßenverkehrs-Gesetz"));
		json.addToArray("tatbestandGroups", new TatbestandGroup("Ferienreise-Verordnung"));
		json.addToArray("tatbestandGroups", new TatbestandGroup("Mobilitätshilfen-Verordnung"));
		json.addToArray("tatbestandGroups", new TatbestandGroup("Tabellen"));
		json.addToArray("tatbestandGroups", new TatbestandGroup("Fahrzeug-Zulassungsverordnung"));
		json.addToArray("tatbestandGroups", new TatbestandGroup("Sonstige"));
		for (Tatbestand tatbestand : tatbestands) {
			getTatbestandGroupByTbnr(tatbestand.getTbnr()).addTatbestand(tatbestand);
		}
	}

	public Tatbestand getTatbestand(int tbnr) {
		return getTatbestandGroupByTbnr(tbnr).getTatbestand(tbnr);
	}

	public TatbestandGroup getTatbestandGroupByTbnr(int tbnr) {
		int idx = (tbnr / 100000) - 1;
		return getTatbestandGroups().get(idx);
	}

	public List<TatbestandGroup> getTatbestandGroups() {
		return getWrapperArray("tatbestandGroups", TatbestandGroup.class);
	}

	public static class TatbestandGroup extends AJsonWrapper {

		public TatbestandGroup(JsonObject json) {
			super(json);
		}

		public int getIndex() {
			return getParent(Tatbestandskatalog.class).getTatbestandGroups().indexOf(this);
		}

		private void addTatbestand(Tatbestand tatbestand) {
			json.addToArray("tatbestands", tatbestand);
		}

		public TatbestandGroup(String law) {
			putMandatory("law", law);
		}

		public String getLaw() {
			return json.getString("law");
		}

		public List<Tatbestand> getTatbestands() {
			return getWrapperArray("tatbestands", Tatbestand.class);
		}

		public Tatbestand getTatbestand(int tbnr) {
			for (Tatbestand tb : getTatbestands()) {
				if (tbnr == tb.getTbnr().intValue()) return tb;
			}
			return null;
		}
	}

	public static class Tatbestand extends AJsonWrapper {

		public Tatbestand(JsonObject json) {
			super(json);
		}

		public Tatbestand(int tbnr, String text, String referencesText, String fapPkt, Integer euro, Integer fv) {
			json.put("tbnr", tbnr);
			putMandatory("text", text);
			json.put("referencesText", referencesText);
			putMandatory("fapPkt", fapPkt);
			json.put("euro", euro);
			json.put("fv", fv);
		}

		private String header;

		public String getHeaderAsHtml() {
			if (header == null) {
				header = "<strong>" + getTbnr() + "</strong> " + getText();
			}
			return header;
		}

		public Integer getTbnr() {
			return json.getInteger("tbnr");
		}

		public String getText() {
			return json.getString("text");
		}

		public String getReferencesText() {
			return json.getString("referencesText");
		}

		public String getFapPkt() {
			return json.getString("fapPkt");
		}

		public Integer getEuro() {
			return json.getInteger("euro");
		}

		public String getEuroFormated() {
			Integer euro = getEuro();
			if (euro == null) return "-";
			String s = String.valueOf(euro);
			int idx = s.length() - 2;
			return s.substring(0, idx) + "," + s.substring(idx) + " EUR";
		}

		public Integer getFv() {
			return json.getInteger("fv");
		}

		public String getFvFormated() {
			Integer fv = getFv();
			if (fv == null) return "-";
			return fv + (fv > 1 ? " Monate" : " Monat");
		}

		@Override
		public String toString() {
			return getTbnr() + " | " + getText() + " | " + getFapPkt() + " | " + getEuro() + " | " + getFv();
		}

	}
}
