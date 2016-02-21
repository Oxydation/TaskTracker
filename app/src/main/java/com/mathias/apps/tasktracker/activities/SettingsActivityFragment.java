package com.mathias.apps.tasktracker.activities;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.mathias.apps.tasktracker.R;
import com.mathias.apps.tasktracker.SeekBarPreferenceAutoSummary;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsActivityFragment extends PreferenceFragment {

    public static final String SETTINGS_SHARED_PREFERENCES_FILE_NAME = SettingsActivityFragment.class.getName() + ".SETTINGS_SHARED_PREFERENCES_FILE_NAME";
    private static final String LOGTAG = "TASKTRACKER";

    public SettingsActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // http://stackoverflow.com/questions/17880437/which-settings-file-does-preferencefragment-read-write
        // Define the settings file to use by this settings fragment
        getPreferenceManager().setSharedPreferencesName(SETTINGS_SHARED_PREFERENCES_FILE_NAME);


        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        SeekBarPreferenceAutoSummary workDuration = (SeekBarPreferenceAutoSummary) findPreference("work_duration");
        SeekBarPreferenceAutoSummary breakDuration = (SeekBarPreferenceAutoSummary) findPreference("break_duration");
        SeekBarPreferenceAutoSummary longBreakDuration = (SeekBarPreferenceAutoSummary) findPreference("long_break_duration");
        SeekBarPreferenceAutoSummary longBreakInterval = (SeekBarPreferenceAutoSummary) findPreference("long_break_interval");

        Log.i(LOGTAG, String.valueOf(workDuration.getMax()));
        workDuration.setMax(60);
        breakDuration.setMax(10);
        longBreakDuration.setMax(20);
        longBreakInterval.setMax(5);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        return inflater.inflate(R.layout.fragment_settings, container, false);
//    }
}
