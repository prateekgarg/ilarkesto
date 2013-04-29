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
package ilarkesto.integration.hochschulkompass;

public class HochschulkompassSearchUrl {

	public static void main(String[] args) {
		System.out.println(new HochschulkompassSearchUrl().toString());
		System.out.println(new HochschulkompassSearchUrl().setSachgr(259).toString());
	}

	public static String getBaseUrl() {
		return "http://www.hochschulkompass.de/studium/suche/profisuche.html";
	}

	private Integer studtyp = 1; // 1:grundständig 3:beide
	private int resultsAtaTime = 99999;
	private Integer sachgr;
	private String namek;
	private String traegerschaft;
	private Integer x;
	private Integer y;
	private String name;
	private Integer sachidx;
	private String sachal;
	private String studbeit;
	private String zusemester;
	private String zubesch;
	private String lehramt;
	private String plz;
	private String ort;
	private String fach;
	private Integer xtend = 1;
	private String genios;
	private Integer search = 1;
	private String sprache;

	public HochschulkompassSearchUrl setSachgr(Integer sachgr) {
		this.sachgr = sachgr;
		return this;
	}

	public HochschulkompassSearchUrl setStudtyp(Integer studtyp) {
		this.studtyp = studtyp;
		return this;
	}

	public HochschulkompassSearchUrl setResultsAtaTime(int resultsAtaTime) {
		this.resultsAtaTime = resultsAtaTime;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getBaseUrl());
		sb.append("?");
		sb.append("tx_szhrksearch_pi1[search]=").append(search);
		sb.append("&tx_szhrksearch_pi1[results_at_a_time]=").append(resultsAtaTime);
		sb.append("&tx_szhrksearch_pi1[xtend]=").append(xtend);
		sb.append("&tx_szhrksearch_pi1[studtyp]=").append(studtyp);
		if (genios != null) sb.append("&genios=").append(genios);
		if (fach != null) sb.append("&tx_szhrksearch_pi1[fach]=").append(fach);
		if (ort != null) sb.append("&tx_szhrksearch_pi1[ort]=").append(ort);
		if (plz != null) sb.append("&tx_szhrksearch_pi1[plz]=").append(plz);
		if (lehramt != null) sb.append("&tx_szhrksearch_pi1[lehramt]=").append(lehramt);
		if (sprache != null) sb.append("&tx_szhrksearch_pi1[sprache]=").append(sprache);
		if (zubesch != null) sb.append("&tx_szhrksearch_pi1[zubesch]=").append(zubesch);
		if (zusemester != null) sb.append("&tx_szhrksearch_pi1[zusemester]=").append(zusemester);
		if (studbeit != null) sb.append("&tx_szhrksearch_pi1[studbeit]=").append(studbeit);
		if (sachal != null) sb.append("&tx_szhrksearch_pi1[sachal]=").append(sachal);
		if (sachgr != null) sb.append("&tx_szhrksearch_pi1[sachgr]=").append(sachgr);
		if (sachidx != null) sb.append("&tx_szhrksearch_pi1[sachidx]=").append(sachidx);
		if (name != null) sb.append("&tx_szhrksearch_pi1[name]=").append(name);
		if (traegerschaft != null) sb.append("&tx_szhrksearch_pi1[traegerschaft]=").append(traegerschaft);
		if (namek != null) sb.append("&tx_szhrksearch_pi1[namek]=").append(namek);
		if (x != null) sb.append("&x=").append(x);
		if (y != null) sb.append("&y=").append(y);
		return sb.toString();
	}

}
