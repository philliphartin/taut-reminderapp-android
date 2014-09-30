package com.phorloop.tautreminders.view.fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.phorloop.tautreminders.R;

/**
 * Created by philliphartin on 30/09/2014.
 */
public class SettingsFragment extends PreferenceFragment {
    private static final String LOGf = "SettingsFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        populateVersionAndBuildDetails();


        //FIXME: Remove this and replace after settings fragment
//        preferencesHelper = new PreferencesHelper(getActivity());
//        preferencesHelper.saveIntialised(true);
    }

    private void populateVersionAndBuildDetails() {
        ListPreference versionPref = (ListPreference) findPreference("version");
        versionPref.setTitle("Version" + ": " + getVersionNumber());
        versionPref.setSummary("Build" + ": " + getBuildNumber());
    }

    private String getVersionNumber() {
        String version;
        try {
            version = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = "Undefined";
            Log.e(LOGf, "Error: " + e);
        }
        return version;
    }

    private String getBuildNumber() {
        String build;
        try {
            int buildNumber = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode;
            build = String.valueOf(buildNumber);
        } catch (PackageManager.NameNotFoundException e) {
            build = "Undefined";
            Log.e(LOGf, "Error: " + e);
        }
        return build;
    }
}
