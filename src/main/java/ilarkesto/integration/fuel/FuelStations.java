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

import ilarkesto.json.JsonObject;
import ilarkesto.json.JsonWrapperList;

import java.util.ArrayList;
import java.util.List;

public class FuelStations extends JsonWrapperList<FuelStation> {

	public FuelStations(JsonObject json) {
		super(FuelStation.class, json);
	}

	public static List<FuelStation> createRintelnStations() {
		List<FuelStation> ret = new ArrayList<FuelStation>();
		ret.add(new FuelStation("Aral", "Auf Der Bünte 1", "519b48f7bed7139bf0d42d4d"));
		ret.add(new FuelStation("Esso (Nord)", "Konrad-Adenauer-Straße 24", "519b49fbbed7139bf0d4427b"));
		ret.add(new FuelStation("Jet", "Konrad-Adenauer-Straße 46, Rinteln", "519b48f4bed7139bf0d42cfa"));
		ret.add(new FuelStation("Esso (Süd)", "Seetorstraße 16, Rinteln", "519b49fcbed7139bf0d4428c"));
		ret.add(new FuelStation("Jantzon (Eisbergen)", "Weserstraße 83, Porta-Westfalica", "519b4b43bed7139bf0d45469"));
		ret.add(new FuelStation("Shell (Luhden)", "An der B 83, Luhden", "519b48f8bed7139bf0d42d65"));
		ret.add(new FuelStation("Harting (Kleinbremen)", "Kleinbremer Straße 4, Porta Westfalica",
				"519b4a92bed7139bf0d44b79"));
		ret.add(new FuelStation("Shell (Möllenbeck)", "Lemgoer Straße 55", "519b48f8bed7139bf0d42d66"));
		// ret.add(new FuelStation("HEM", "Hannoversche Straße 16, Bückeburg", "519b49bfbed7139bf0d43e77"));
		return ret;
	}
}
