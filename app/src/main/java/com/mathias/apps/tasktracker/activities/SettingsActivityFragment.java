package com.mathias.apps.tasktracker.activities;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.mathias.apps.tasktracker.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsActivityFragment extends PreferenceFragment {

    public static final String SETTINGS_SHARED_PREFERENCES_FILE_NAME = SettingsActivityFragment.class.getName() + ".SETTINGS_SHARED_PREFERENCES_FILE_NAME";

    public SettingsActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Define the settings file to use by this settings fragment
        getPreferenceManager().setSharedPreferencesName(SETTINGS_SHARED_PREFERENCES_FILE_NAME);


        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        return inflater.inflate(R.layout.fragment_settings, container, false);
//    }
}
