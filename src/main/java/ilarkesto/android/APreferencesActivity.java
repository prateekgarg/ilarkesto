package ilarkesto.android;

import java.util.Map;

import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public abstract class APreferencesActivity<C extends ConfigStore> extends PreferenceActivity {

	protected APreferencesActivity context = this;

	protected abstract C createConfig();

	protected abstract void initPreferences();

	private C configStore;
	private PreferenceScreen preferenceScreen;

	@Override
	protected final void onStart() {
		super.onStart();
		AApp.get().activityStart(this);

		PreferenceManager preferenceManager = getPreferenceManager();
		preferenceManager.setSharedPreferencesName(getConfig().getConfigFilename());
		preferenceScreen = preferenceManager.createPreferenceScreen(this);
		initPreferences();
		setPreferenceScreen(preferenceScreen);
	}

	public final synchronized C getConfig() {
		if (configStore == null) configStore = createConfig();
		return configStore;
	}

	@Override
	protected void onStop() {
		super.onStop();
		AApp.get().activityStop(this);
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
