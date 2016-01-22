package com.ganwal.nanodegree.popularMovie;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

/**
 * Shows app Settings, currently user can only change movies sort order
 */

public class MovieSettingsActivity extends PreferenceActivity {

    public static final String LOG_TAG = MovieSettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MovieSettingsFragment()).commit();

    }


    //******************************** MovieSettingsFragment *************************************//
    public static class MovieSettingsFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        final String LOG_TAG = MovieSettingsFragment.class.getSimpleName();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = super.onCreateView(inflater, container, savedInstanceState);
            Preference pref = getPreferenceManager().findPreference("pref_sort_order");
            if(pref != null) {
                pref.setOnPreferenceChangeListener(this);
                if (pref instanceof ListPreference) {
                    ListPreference listPreference = (ListPreference) pref;
                    String existingVal = listPreference.getValue();
                    pref.setSummary(listPreference.getEntry());
                }
            }
            return rootView;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String strValue = newValue.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(strValue);
                if (prefIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            }
            return true;
        }
    }


}
