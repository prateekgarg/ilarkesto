package ilarkesto.android;

import ilarkesto.core.base.Str;
import ilarkesto.core.base.Utl;
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
		this.id = id;
	}

	public long getLong(String key, long defValue) {
		return getPrefs().getLong(key, defValue);
	}

	public void setLong(String key, long value) {
		if (value == getLong(key, 0)) return;
		Editor editor = getPrefs().edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public boolean getBool(String key, boolean defValue) {
		return getPrefs().getBoolean(key, defValue);
	}

	public void setBool(String key, boolean value) {
		if (value == getBool(key, false)) return;
		Editor editor = getPrefs().edit();
		editor.putBoolean(key, value);
		editor.commit();
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
			onValueChanged();
		}

		protected void onValueChanged() {}

	}

	public abstract class AStringConf extends AConf {

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
			onValueChanged();
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
	}

}
