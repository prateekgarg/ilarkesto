package ilarkesto.core.navig;

import ilarkesto.core.base.Str;

import java.util.LinkedHashMap;
import java.util.Map;

public class Item {

	private Plugin plugin;
	private Object payload;
	private String label;
	private Map<String, String> properties = new LinkedHashMap<String, String>();

	public Item(Plugin plugin, String label) {
		super();
		this.plugin = plugin;
		this.label = label;
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public void setProperty(String label, String value) {
		properties.put(label, value);
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

	public Object getPayload() {
		return payload;
	}

	@Override
	public String toString() {
		return Str.toStringHelper(this, plugin, label);
	}

}
