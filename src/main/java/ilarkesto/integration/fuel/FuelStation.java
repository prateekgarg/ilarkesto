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

import ilarkesto.core.base.Utl;
import ilarkesto.core.time.DateAndTime;
import ilarkesto.core.time.Time;
import ilarkesto.json.AJsonWrapper;
import ilarkesto.json.JsonObject;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

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

	public FuelStation addServiceTime(Time from, Time to) {
		ServiceTime serviceTime = new ServiceTime(from, to);
		json.addToArray("serviceTimes", serviceTime);
		return this;
	}

	public FuelStation addServiceTime(int fromHour, int toHour) {
		return addServiceTime(new Time(fromHour, 0), new Time(toHour, 0));
	}

	public List<ServiceTime> getServiceTimes() {
		return getWrapperArray("serviceTimes", ServiceTime.class);
	}

	public boolean isServiceTime(DateAndTime dateAndTime) {
		List<ServiceTime> serviceTimes = getServiceTimes();
		if (serviceTimes.isEmpty()) return true;
		for (ServiceTime serviceTime : serviceTimes) {
			if (serviceTime.isServiceTime(dateAndTime)) return true;
		}
		return false;
	}

	public boolean isServiceTime() {
		return isServiceTime(DateAndTime.now());
	}

	@Override
	public String toString() {
		return getLabel();
	}

	public static class Price extends AJsonWrapper implements Comparable<Price> {

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
			return format(getPriceAsFloat());
		}

		@Override
		public String toString() {
			return getPriceAsString();
		}

		@Override
		public int compareTo(Price o) {
			return Utl.compare(getPriceTicks(), o.getPriceTicks());
		}

		public static String format(float price) {
			BigDecimal bd = new BigDecimal(price);
			bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
			return bd.toString().replace('.', ',');
		}
	}

	public static class ServiceTime extends AJsonWrapper {

		public ServiceTime(Time from, Time to) {
			json.put("from", from.toString());
			json.put("to", to.toString());
		}

		public ServiceTime(JsonObject json) {
			super(json);
		}

		public Time getFrom() {
			return new Time(json.getString("from"));
		}

		public Time getTo() {
			return new Time(json.getString("to"));
		}

		public boolean isServiceTime(DateAndTime dateAndTime) {
			return dateAndTime.getTime().isBetween(getFrom(), getTo());
		}

	}

	public static class PriceComparator implements Comparator<FuelStation> {

		private String fuel;

		public PriceComparator(String fuel) {
			super();
			this.fuel = fuel;
		}

		@Override
		public int compare(FuelStation a, FuelStation b) {
			return Utl.compare(a.getLatestPriceByFuel(fuel), b.getLatestPriceByFuel(fuel));
		}

	}

}
