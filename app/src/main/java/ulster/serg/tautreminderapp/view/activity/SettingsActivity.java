package ulster.serg.tautreminderapp.view.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import ulster.serg.tautreminderapp.controller.demo.DemoReminders;
import ulster.serg.tautreminderapp.controller.helpers.PreferencesHelper;

/**
 * Created by philliphartin on 30/09/2014.
 */
public class SettingsActivity extends Activity {
    private static final String LOGa = "SettingsActivity";
    private static SettingsFragment settingsFragment;
    private static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //Init sharedPreferences and settings fragment
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        settingsFragment = new SettingsFragment();

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, settingsFragment)
                .commit();

        ActionBar actionBar = getActionBar();
        actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Settings");
    }

    @Override
    public void onBackPressed() {
        PreferencesHelper preferencesHelper = new PreferencesHelper(this);

        if (checkAllGood()) {
            preferencesHelper.saveIntialised(true);
            super.onBackPressed();
        } else {
            //Do nothing
            //preferencesHelper.saveIntialised(false);
            Toast.makeText(this, "Ensure all details are entered", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return true;
    }

    public static class SettingsFragment extends PreferenceFragment {
        private static final String LOGf = "SettingsFragment";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(ulster.serg.tautreminderapp.R.xml.preferences);
            populateVersionAndBuildDetails();
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        private void populateVersionAndBuildDetails() {
            Preference version = findPreference("version");
            version.setTitle("Version" + ": " + getVersionNumber());
            version.setSummary("Build" + ": " + getBuildNumber());
            version.setSelectable(true);
            version.setOnPreferenceClickListener(onClickDemoCreator);
        }

        private Preference.OnPreferenceClickListener onClickDemoCreator = new Preference.OnPreferenceClickListener() {

            int count = 0;
            int numberPressRequired = 8;
            Toast mToast;

            public boolean onPreferenceClick(Preference pref) {

                count = count + 1;
                int remaining = calculateRemainingPresses();

                if (readyForDisplay(remaining)) {

                    if (mToast == null) {
                        mToast = Toast.makeText(getActivity(), "Press " + remaining + " more times to create demo reminders", Toast.LENGTH_LONG);
                    }
                    mToast.setText("Press " + remaining + " more times to create demo reminders");
                    mToast.setDuration(Toast.LENGTH_LONG);
                    mToast.show();
                }

                if (numberRemainingIsZero(remaining)) {
                    mToast.cancel(); //Cancel the toast
                    DemoReminders demoReminders = new DemoReminders(getActivity());
                    demoReminders.generateRemindersForDemos();
                }
                return false;
            }

            private int calculateRemainingPresses() {
                return numberPressRequired - count;
            }

            private boolean readyForDisplay(int remainingPresses) {
                if (remainingPresses <= 5 && remainingPresses > 0) {
                    return true;
                } else {
                    return false;
                }
            }

            private boolean numberRemainingIsZero(int remaining) {
                if (remaining == 0) {
                    return true;
                } else {
                    return false;
                }
            }
        };

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

        private void setAsInitialised() {
            PreferencesHelper preferencesHelper = new PreferencesHelper(getActivity());
            preferencesHelper.saveIntialised(true);
        }
    } // End of Fragment


    //Check User Methods
    public boolean checkUserSetup() {

        if (userNameEntered() & userIdEntered()) {
            return true;
        } else {
            Toast.makeText(this, "You must set the user's details", Toast.LENGTH_SHORT).show();

            return false;
        }
    }

    private boolean userNameEntered() {
        String key = "user_name";
        return stringIsValidForKey(key);
    }

    private boolean userIdEntered() {
        String key = "user_id";
        return stringIsValidForKey(key);
    }

    public boolean checkCarer1() {

        if (checkCarerSelected_1()) {
            if (checkCarerName_1() & checkCarerId_1()) {
                return true;
            } else {
                Toast.makeText(this, "You must set 1st carer's details", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            //Do Nothing
            return true;
        }
    }

    private boolean checkCarerSelected_1() {
        return sharedPreferences.getBoolean("carer_exist", false);
    }

    private boolean checkCarerName_1() {
        String key = "carer_name";
        return stringIsValidForKey(key);
    }

    private boolean checkCarerId_1() {
        String key = "carer_id";
        return stringIsValidForKey(key);
    }


    public boolean checkCarer2() {

        if (checkCarerSelected_2()) {
            if (checkCarerName_2() & checkCarerId_2()) {
                return true;
            } else {
                Toast.makeText(this, "You must set 2nd carer's details", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            //Do Nothing
            return true;
        }
    }

    private boolean checkCarerSelected_2() {
        return sharedPreferences.getBoolean("carer_exist2", false);
    }

    private boolean checkCarerName_2() {
        String key = "carer_name2";
        return stringIsValidForKey(key);
    }

    private boolean checkCarerId_2() {
        String key = "carer_id2";
        return stringIsValidForKey(key);
    }


    private boolean stringIsValidForKey(String key) {
        String string = sharedPreferences.getString(key, "");
        if (string.length() > 0) {
            return true;
        } else {
            Log.d(LOGa, "String for key: " + key + " is empty");
            return false;
        }
    }

    public boolean checkAllGood() {
        Boolean userSetup = checkUserSetup();
        Boolean carer1Setup = checkCarer1();
        Boolean carer2Setup = checkCarer2();

        if (userSetup & carer1Setup & carer2Setup) {
            return true;
        } else {
            return false;
        }
    }

}
