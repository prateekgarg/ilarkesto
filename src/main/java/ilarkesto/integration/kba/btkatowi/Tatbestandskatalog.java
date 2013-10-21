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

import ilarkesto.core.base.Str;
import ilarkesto.json.AJsonWrapper;
import ilarkesto.json.JsonObject;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

		public String getLawBookCode() {
			switch (getIndex()) {
				case 0:
					return "StVO";
				case 1:
					return "FeV";
				case 2:
					return "StVZO";
				case 3:
					return "StVG";
				case 4:
					return "BKatV";
				case 5:
					return "MobHV";
				case 6:
					return null;
				case 7:
					return "FZV";
				default:
					return null;
			}
		}

		public Set<String> getLawNormCodes() {
			Set<String> codes = new LinkedHashSet<String>();
			for (Tatbestand tb : getTatbestands()) {
				codes.add(tb.getLawNormCode());
			}
			return codes;
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

		public List<Tatbestand> getTatbestandsByLawCode(String lawNormCode) {
			List<Tatbestand> ret = new LinkedList<Tatbestandskatalog.Tatbestand>();
			for (Tatbestand tb : getTatbestands()) {
				if (lawNormCode.equals(tb.getLawNormCode())) ret.add(tb);
			}
			return ret;
		}

		public Tatbestand getTatbestand(int tbnr) {
			for (Tatbestand tb : getTatbestands()) {
				if (tbnr == tb.getTbnr().intValue()) return tb;
			}
			return null;
		}
	}

	public static String getNormLabel(String bookCode, String normCode) {
		if ("StVO".equals(bookCode)) {
			if ("1".equals(normCode)) return "Grundregeln";
			if ("2".equals(normCode)) return "Straßenbenutzung";
			if ("3".equals(normCode)) return "Geschwindigkeit";
			if ("4".equals(normCode)) return "Abstand";
			if ("5".equals(normCode)) return "Überholen";
			if ("6".equals(normCode)) return "Vorbeifahren";
			if ("7".equals(normCode)) return "Fahrstreifen";
			if ("7a".equals(normCode)) return "Abgehende";
			if ("8".equals(normCode)) return "Vorfahrt";
			if ("9".equals(normCode)) return "Abbiegen";
			if ("10".equals(normCode)) return "Einfahren";
			if ("11".equals(normCode)) return "Verkehrslagen";
			if ("12".equals(normCode)) return "Halten/Parken";
			if ("13".equals(normCode)) return "Parkzeit";
			if ("14".equals(normCode)) return "Ein- und Aussteigen";
			if ("15".equals(normCode)) return "Liegenbleiben";
			if ("15a".equals(normCode)) return "Abschleppen";
			if ("16".equals(normCode)) return "Warnzeichen";
			if ("17".equals(normCode)) return "Beleuchtung";
			if ("18".equals(normCode)) return "Kraftfahrstraßen";
			if ("19".equals(normCode)) return "Bahnübergänge";
			if ("20".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
			if ("".equals(normCode)) return "";
		}
		return null;
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

		public String getLawNormCode() {
			String code = getTbnr().toString().substring(1, 3);
			code = Str.removePrefix(code, "0");
			return code;
		}

		public TatbestandGroup getGroup() {
			return getParent(TatbestandGroup.class);
		}

		public boolean matchesWords(List<String> words) {
			for (String word : words) {
				if (!matchesWord(word)) return false;
			}
			return true;
		}

		private boolean matchesWord(String word) {
			if (getTbnr().toString().contains(word)) return true;
			if (getText().toLowerCase().contains(word)) return true;
			return false;
		}

		private String header;

		public String getHeaderAsHtml() {
			if (header == null) {
				String tbnr = getTbnr().toString();
				String lawNr = tbnr.substring(0, 1);
				String normNr = tbnr.substring(1, 3);
				String nr = tbnr.substring(3);
				header = "<strong>" + lawNr + "</strong><em>" + normNr + "</em><strong>" + nr + "</strong> "
						+ getText();
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

		public String getAnnotations() {
			return json.getString("annotations");
		}

		public void appendAnnotationLine(String line) {
			String annotations = getAnnotations();
			if (annotations == null) {
				annotations = "";
			} else {
				annotations += "\n";
			}
			annotations += line;
			json.put("annotations", annotations);
		}

		@Override
		public String toString() {
			return getTbnr() + " | " + getText() + " | " + getFapPkt() + " | " + getEuro() + " | " + getFv();
		}

	}
}
