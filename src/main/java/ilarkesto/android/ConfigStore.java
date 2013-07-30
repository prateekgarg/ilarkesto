package ilarkesto.android;

import ilarkesto.core.base.Str;
import ilarkesto.core.base.Utl;
import ilarkesto.json.JsonObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ConfigStore {

	protected final Context context;
	private final String id;
	private SharedPreferences sharedPreferences;

	public ConfigStore(Context context, String id) {
		super();
		this.context = context;
		this.id = id == null ? getClass().getSimpleName() : id;
	}

	public ConfigStore(Context context) {
		this(context, null);
	}

	public long getLong(String key, long defValue) {
		return getPrefs().getLong(key, defValue);
	}

	public void setLong(String key, long value) {
		if (value == getLong(key, 0)) return;
		Editor editor = getPrefs().edit();
		editor.putLong(key, value);
		editor.commit();
		AAndroidTracker.get().preferenceChange(getConfigId() + "." + key, Str.format(value));
	}

	public boolean getBool(String key, boolean defValue) {
		return getPrefs().getBoolean(key, defValue);
	}

	public void setBool(String key, boolean value) {
		if (value == getBool(key, false)) return;
		Editor editor = getPrefs().edit();
		editor.putBoolean(key, value);
		editor.commit();
		AAndroidTracker.get().preferenceChange(getConfigId() + "." + key, Str.format(value));
	}

	public String get(String key) {
		return get(key, null);
	}

	public String get(String key, String defValue) {
		return getPrefs().getString(key, defValue);
	}

	public void set(String key, String value) {
		String currentValue = get(key);
		if (value == null) {
			if (currentValue == null) return;
		} else {
			if (value.equals(currentValue)) return;
		}
		Editor editor = getPrefs().edit();
		editor.putString(key, value);
		editor.commit();
		AAndroidTracker.get().preferenceChange(getConfigId() + "." + key, Str.format(value));
	}

	public SharedPreferences getPrefs() {
		if (sharedPreferences == null)
			sharedPreferences = context.getSharedPreferences(getConfigFilename(), Context.MODE_PRIVATE);

		return sharedPreferences;
	}

	public final String getConfigId() {
		return id;
	}

	public final String getConfigFilename() {
		return id + ".preferences";
	}

	public abstract class ABooleanConf extends AConf {

		protected boolean getDefaultValue() {
			return false;
		}

		public final boolean isTrue() {
			return getPrefs().getBoolean(getKey(), getDefaultValue());
		}

		public final boolean isFalse() {
			return !isTrue();
		}

		public final void setValue(boolean value) {
			if (isTrue() == value) return;
			Editor editor = getPrefs().edit();
			editor.putBoolean(getKey(), value);
			editor.commit();
			AAndroidTracker.get().preferenceChange(getTrackingId(), Str.format(value));
			onValueChanged();
		}

		protected void onValueChanged() {}

	}

	public abstract class AIntegerConf extends AConf {

		public Integer getValue(int defaultValue) {
			return getPrefs().getInt(getKey(), defaultValue);
		}

		public Integer getValue() {
			Integer value = getValue(Integer.MIN_VALUE);
			if (value == Integer.MIN_VALUE) return null;
			return value;
		}

		public void setValue(Integer value) {
			if (getValue() == value) return;
			Editor editor = getPrefs().edit();
			editor.putInt(getKey(), value);
			editor.commit();
			AAndroidTracker.get().preferenceChange(getTrackingId(), Str.format(value));
			onValueChanged();
		}

		protected void onValueChanged() {}

	}

	public abstract class ALongConf extends AConf {

		public Long getValue(int defaultValue) {
			return getPrefs().getLong(getKey(), defaultValue);
		}

		public Long getValue() {
			Long value = getValue(Integer.MIN_VALUE);
			if (value == Long.MIN_VALUE) return null;
			return value;
		}

		public void setValue(Long value) {
			if (getValue() == value) return;
			Editor editor = getPrefs().edit();
			editor.putLong(getKey(), value);
			editor.commit();
			AAndroidTracker.get().preferenceChange(getTrackingId(), Str.format(value));
			onValueChanged();
		}

		protected void onValueChanged() {}

	}

	public abstract class AStringConf extends AConf {

		public boolean isValue(String s) {
			String value = getValue();
			return value == null ? s == null : value.equals(s);
		}

		public String getValue() {
			return getPrefs().getString(getKey(), getDefaultValue());
		}

		public boolean isBlanc() {
			return Str.isBlank(getValue());
		}

		protected String getDefaultValue() {
			return null;
		}

		public void setValue(String value) {
			if (Utl.equals(value, getValue())) return;
			Editor editor = getPrefs().edit();
			String key = getKey();
			editor.putString(key, value);
			editor.commit();
			AAndroidTracker.get().preferenceChange(getTrackingId(), Str.format(value));
			onValueChanged();
		}

		protected void onValueChanged() {}

	}

	public abstract class AStringListConf extends AConf {

		public List<String> getValue() {
			String json = getPrefs().getString(getKey(), null);
			if (json == null) return getDefaultValue();
			JsonObject jo = new JsonObject(json);
			return jo.getArrayOfStrings("list");
		}

		private List<String> getDefaultValue() {
			return new LinkedList<String>();
		}

		public void setValue(List<String> value) {
			if (Utl.equals(value, getValue())) return;
			Editor editor = getPrefs().edit();
			String key = getKey();
			JsonObject jo = new JsonObject();
			jo.put("list", value);
			editor.putString(key, value == null ? null : jo.toString());
			editor.commit();
			AAndroidTracker.get().preferenceChange(getTrackingId(), Str.format(value));
			onValueChanged();
		}

		public void addValueItem(int index, String item) {
			addValueItem(index, item, false);
		}

		public void addValueItem(int index, String item, boolean removeFirst) {
			List<String> value = getValue();
			if (removeFirst) value.remove(item);
			value.add(index, item);
			setValue(value);
		}

		public void addValueItem(String item) {
			addValueItem(item, false);
		}

		public void removeValueItem(String item) {
			List<String> value = getValue();
			value.remove(item);
			setValue(value);
		}

		public void addValueItems(Collection<String> items) {
			List<String> value = getValue();
			value.addAll(items);
			setValue(value);
		}

		public void addValueItem(String item, boolean removeFirst) {
			List<String> value = getValue();
			if (removeFirst) value.remove(item);
			value.add(item);
			setValue(value);
		}

		protected void onValueChanged() {}

	}

	public abstract class AJsonConf extends AConf {

		public JsonObject getValue() {
			String json = getPrefs().getString(getKey(), null);
			if (json == null) return getDefaultValue();
			return new JsonObject(json);
		}

		private JsonObject getDefaultValue() {
			return null;
		}

		public void setValue(JsonObject value) {
			if (Utl.equals(value, getValue())) return;
			Editor editor = getPrefs().edit();
			String key = getKey();
			editor.putString(key, value == null ? null : value.toString());
			editor.commit();
			AAndroidTracker.get().preferenceChange(getTrackingId(), Str.format(value));
			onValueChanged();
		}

		protected void onValueChanged() {}
	}

	public abstract class AStringSetConf extends AConf {

		public Set<String> getValue() {
			return getPrefs().getStringSet(getKey(), getDefaultValue());
		}

		private Set<String> getDefaultValue() {
			return new HashSet<String>();
		}

		public void setValue(Set<String> value) {
			if (Utl.equals(value, getValue())) return;
			Editor editor = getPrefs().edit();
			String key = getKey();
			editor.putStringSet(key, value);
			editor.commit();
			AAndroidTracker.get().preferenceChange(getTrackingId(), Str.format(value));
			onValueChanged();
		}

		public void addValueItem(String item) {
			Set<String> value = getValue();
			value.add(item);
			setValue(value);
		}

		public void removeValueItem(String item) {
			Set<String> value = getValue();
			value.remove(item);
			setValue(value);
		}

		protected void onValueChanged() {}

	}

	public abstract class AConf {

		public String getTitle() {
			return getKey();
		}

		public String getSummary() {
			return null;
		}

		public String getKey() {
			String key = getClass().getSimpleName();
			int idx = key.indexOf('$');
			if (idx > 0) key = key.substring(idx + 1);
			return key;
		}

		public void remove() {
			Editor editor = getPrefs().edit();
			String key = getKey();
			editor.remove(key);
			editor.commit();
		}

		String getTrackingId() {
			return getConfigId() + "." + getKey();
		}
	}

}
