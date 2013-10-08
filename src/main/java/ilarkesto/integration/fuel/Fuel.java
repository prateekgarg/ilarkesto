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
package ilarkesto.integration.fuel;

import java.util.ArrayList;
import java.util.List;

public class Fuel {

	public static final String DIESEL = "DIESEL";
	public static final String E5 = "E5";
	public static final String E10 = "E10";
	public static final String PLUS = "PLUS";

	public static String getFuelLabel(String fuelType) {
		if (DIESEL.equals(fuelType)) return "Diesel";
		if (E5.equals(fuelType)) return "Super 95";
		if (E10.equals(fuelType)) return "Super E10";
		if (PLUS.equals(fuelType)) return "Super Plus";
		return fuelType;
	}

	public static List<FuelStation> createRintelnStations() {
		List<FuelStation> ret = new ArrayList<FuelStation>();
		ret.add(new FuelStation("Jet (Mc Donalds)", "Konrad-Adenauer-Straße 46, Rinteln", "519b48f4bed7139bf0d42cfa"));
		ret.add(new FuelStation("Aral (Kino)", "Auf Der Bünte 1, Rinteln", "519b48f7bed7139bf0d42d4d"));
		ret.add(new FuelStation("Esso (Marktkauf)", "Konrad-Adenauer-Straße 24, Rinteln", "519b49fbbed7139bf0d4427b"));
		ret.add(new FuelStation("Esso (Friedhof)", "Seetorstraße 16, Rinteln", "519b49fcbed7139bf0d4428c"));
		ret.add(new FuelStation("Jantzon (Eisbergen)", "Weserstraße 83, Porta-Westfalica", "519b4b43bed7139bf0d45469"));
		ret.add(new FuelStation("Shell (Luhden, A2)", "An der B 83, Luhden", "519b48f8bed7139bf0d42d65"));
		ret.add(new FuelStation("Harting (Kleinbremen)", "Kleinbremer Straße 4, Porta Westfalica",
				"519b4a92bed7139bf0d44b79"));
		ret.add(new FuelStation("Shell (Möllenbeck)", "Lemgoer Straße 55, Rinteln", "519b48f8bed7139bf0d42d66"));
		ret.add(new FuelStation("Reiffeisen (Krankenhagen)", "Extertalstraße 10c, Rinteln", "519b4bcbbed7139bf0d45ac0"));
		// ret.add(new FuelStation("HEM", "Hannoversche Straße 16, Bückeburg", "519b49bfbed7139bf0d43e77"));
		return ret;
	}

}
