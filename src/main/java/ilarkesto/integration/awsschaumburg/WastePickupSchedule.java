package ilarkesto.integration.awsschaumburg;

import ilarkesto.core.base.Utl;
import ilarkesto.core.time.Date;
import ilarkesto.id.Identifiable;
import ilarkesto.json.AJsonWrapper;
import ilarkesto.json.JsonObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class WastePickupSchedule extends AJsonWrapper implements Identifiable {

	public WastePickupSchedule(JsonObject json) {
		super(json);
	}

	public WastePickupSchedule(WastePickupArea area) {
		super();
		json.put("areaAwsId", area.getAwsId());
	}

	public int getAreaAwsId() {
		return json.getInteger("areaAwsId");
	}

	public List<Pickup> getPickups() {
		return getWrapperArray("pickups", Pickup.class);
	}

	public void addPickupDate(String wasteTypeLabel, Date date) {
		Pickup pickup = getPickupByWasteTypeLabel(wasteTypeLabel);
		if (pickup == null) {
			pickup = new Pickup(wasteTypeLabel);
			json.addToArray("pickups", pickup.getJson());
		}
		pickup.addDate(date);
	}

	public void removePickupDatesBefore(Date minDate) {
		boolean pickupRemoved = false;
		List<Pickup> pickups = getPickups();
		for (Pickup pickup : new ArrayList<Pickup>(pickups)) {
			pickup.removePickupDatesBefore(minDate);
			if (pickup.getDates().isEmpty()) {
				pickups.remove(pickup);
				pickupRemoved = true;
			}
		}
		if (pickupRemoved) putArray("pickups", pickups);
	}

	public Set<String> getWasteTypeLabels() {
		Set<String> ret = new HashSet<String>();
		for (Pickup pickup : getPickups()) {
			ret.add(pickup.getWasteTypeLabel());
		}
		return ret;
	}

	public Pickup getPickupByWasteTypeLabel(String wasteTypeLabel) {
		for (Pickup pickup : getPickups()) {
			if (wasteTypeLabel.equals(pickup.getWasteTypeLabel())) return pickup;
		}
		return null;
	}

	public List<Pickup> getPickupsOnDate(String date) {
		List<Pickup> pickups = new LinkedList<WastePickupSchedule.Pickup>();
		for (Pickup pickup : getPickups()) {
			if (pickup.getDates().contains(date)) pickups.add(pickup);
		}
		return pickups;
	}

	public List<PickupDate> getNextPickupDates() {
		List<PickupDate> ret = new LinkedList<PickupDate>();
		Set<String> types = getWasteTypeLabels();
		for (String type : types) {
			if (containsSubtype(ret, type)) continue;
			Date date = getNextPickupDateByWasteTypeLabel(type);
			if (date == null) continue;
			ret.add(new PickupDate(type, date));
		}
		return ret;
	}

	private boolean containsSubtype(List<PickupDate> pickups, String type) {
		for (PickupDate pickup : pickups) {
			if (pickup.getWasteTypesLabel().contains(type)) return true;
		}
		return false;
	}

	public Date getNextPickupDateByWasteTypeLabel(String wasteTypeLabel) {
		Pickup pickup = getPickupByWasteTypeLabel(wasteTypeLabel);
		if (pickup == null) return null;
		List<String> dates = pickup.getDates();
		if (dates.isEmpty()) return null;
		for (int i = 0; i <= 32; i++) {
			Date date = Date.inDays(i);
			String dateAsString = date.toString();
			if (dates.contains(dateAsString)) return date;
		}
		return null;
	}

	@Override
	public String getId() {
		return "wps:aws:" + getAreaAwsId();
	}

	@Override
	public String toString() {
		return getAreaAwsId() + ":" + getPickups().size();
	}

	public static class Pickup extends AJsonWrapper {

		public Pickup(JsonObject json) {
			super(json);
		}

		public void removePickupDatesBefore(Date minDate) {
			List<String> dates = getDates();
			boolean dateRemoved = false;
			for (String dateS : new ArrayList<String>(dates)) {
				Date date = new Date(dateS);
				if (date == null) continue;
				if (date.isBefore(minDate)) {
					dates.remove(dateS);
					dateRemoved = true;
				}
			}
			if (dateRemoved) json.put("dates", dates);
		}

		public Pickup(String wasteTypeLabel) {
			json.put("wasteTypeLabel", normalizeWasteTypeLabel(wasteTypeLabel));
		}

		public String getWasteTypeLabel() {
			return normalizeWasteTypeLabel(json.getString("wasteTypeLabel"));
		}

		private String normalizeWasteTypeLabel(String s) {
			if (s == null) return s;
			s = s.replace("  ", " ");
			return s;
		}

		public List<String> getDates() {
			List<String> dates = json.getArrayOfStrings("dates");
			if (dates == null) return Collections.emptyList();
			return dates;
		}

		public void addDate(Date date) {
			Set<String> dates = new HashSet<String>(getDates());
			dates.add(date.toString());
			json.put("dates", Utl.sort(dates));
		}

		@Override
		public String toString() {
			return getWasteTypeLabel();
		}

		public static Set<String> getWasteTypeLabels(Collection<Pickup> pickups) {
			Set<String> ret = new HashSet<String>();
			for (Pickup pickup : pickups) {
				ret.add(pickup.getWasteTypeLabel());
			}
			return ret;
		}
	}

	public static class PickupDate implements Comparable<PickupDate> {

		private final String wasteTypesLabel;
		private final Date date;

		private PickupDate(String wasteTypesLabel, Date date) {
			super();
			this.wasteTypesLabel = wasteTypesLabel;
			this.date = date;
		}

		public String getWasteTypesLabel() {
			return wasteTypesLabel;
		}

		public Date getDate() {
			return date;
		}

		@Override
		public int compareTo(PickupDate another) {
			return date.compareTo(another.date);
		}

		@Override
		public String toString() {
			return getWasteTypesLabel() + ": " + getDate();
		}

	}

}
