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

import ilarkesto.json.AJsonWrapper;
import ilarkesto.json.JsonObject;

public class FuelStation extends AJsonWrapper {

	public FuelStation(String label, String address, String tonlineId) {
		putMandatory("label", label);
		putMandatory("tonlineId", tonlineId);
		putMandatory("address", address);
	}

	public String getAddress() {
		return json.getString("address");
	}

	public String getLabel() {
		return json.getString("label");
	}

	public String getTonlineId() {
		return json.getString("tonlineId");
	}

	public Price getLatestPriceByFuel(String fuelId) {
		return getWrapper("latestPrice_" + fuelId, Price.class);
	}

	public void addPrice(String fuelId, Price price) {
		if (price == null) return;
		Price latest = getLatestPriceByFuel(fuelId);
		if (latest != null && price.getTime() < latest.getTime()) return;
		json.put("latestPrice_" + fuelId, price);
	}

	@Override
	public String toString() {
		return getLabel();
	}

	public static class Price extends AJsonWrapper {

		public Price(long priceTicks, long time) {
			putMandatory("priceTicks", priceTicks);
			putMandatory("time", time);
		}

		public Price(JsonObject json) {
			super(json);
		}

		public long getTime() {
			return json.getLong("time");
		}

		public long getPriceTicks() {
			return json.getLong("priceTicks");
		}

		public float getPriceAsFloat() {
			return getPriceTicks() / 1000f;
		}

		public String getPriceAsString() {
			return String.valueOf(getPriceAsFloat()).replace('.', ',');
		}

		@Override
		public String toString() {
			return getPriceAsString();
		}

	}

}
