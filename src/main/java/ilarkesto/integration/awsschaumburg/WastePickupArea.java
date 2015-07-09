package ilarkesto.integration.awsschaumburg;

import ilarkesto.core.base.Identifiable;
import ilarkesto.json.AJsonWrapper;
import ilarkesto.json.JsonObject;

public class WastePickupArea extends AJsonWrapper implements Identifiable {

	public WastePickupArea(JsonObject json) {
		super(json);
	}

	public WastePickupArea(String label, int awsId) {
		json.put("label", label);
		json.put("awsId", awsId);
	}

	public String getLabel() {
		return json.getString("label");
	}

	public int getAwsId() {
		return json.getInteger("awsId");
	}

	@Override
	public String toString() {
		return getLabel();
	}

	@Override
	public String getId() {
		return "aws:" + getAwsId();
	}

}
