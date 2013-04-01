package ilarkesto.android;

import java.util.Map;

import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public abstract class APreferencesActivity extends PreferenceActivity {

	protected abstract ConfigStore getConfig();

	protected abstract void initPreferences();

	private PreferenceScreen preferenceScreen;

	@Override
	protected final void onStart() {
		super.onStart();
		ConfigStore configStore = getConfig();
		AApp.get().getUserTracker().track(this, configStore.getConfigId());

		PreferenceManager preferenceManager = getPreferenceManager();
		preferenceManager.setSharedPreferencesName(configStore.getConfigFilename());
		preferenceScreen = preferenceManager.createPreferenceScreen(this);
		initPreferences();
		setPreferenceScreen(preferenceScreen);
	}

	protected void addPreference(ConfigStore.ABooleanConf conf) {
		preferenceScreen.addPreference(new MyCheckBoxPreference(conf));
	}

	class MyCheckBoxPreference extends CheckBoxPreference {

		private final ConfigStore.ABooleanConf conf;

		public MyCheckBoxPreference(ConfigStore.ABooleanConf conf) {
			super(APreferencesActivity.this);
			this.conf = conf;
			setTitle(conf.getTitle());
			setSummary(conf.getSummary());
			setChecked(conf.isTrue());
		}

		@Override
		protected void onClick() {
			super.onClick();
			conf.setValue(isChecked());
			setSummary(conf.getSummary());
		}

	}

	public abstract class AListPreference extends ListPreference implements OnPreferenceChangeListener {

		protected abstract String getPreferenceTitle();

		protected abstract String getPreferenceSummary();

		protected abstract String getPreferenceKey();

		protected abstract Map<String, String> getPreferenceOptions();

		protected abstract String getPreferenceValue();

		public AListPreference() {
			super(APreferencesActivity.this);
			setTitle(getPreferenceTitle());
			setSummary(getPreferenceSummary());
			setKey(getPreferenceKey());

			Map<String, String> options = getPreferenceOptions();
			// TODO
			setEntries(new String[] { "50%", "80%", "100%", "120%", "150%", "200%" });
			setEntryValues(new String[] { "50", "80", "100", "120", "150", "200" });

			setDefaultValue(getPreferenceValue());

			setOnPreferenceChangeListener(this);
		}

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			setSummary(getPreferenceSummary());
			return true;
		}

	}

}
