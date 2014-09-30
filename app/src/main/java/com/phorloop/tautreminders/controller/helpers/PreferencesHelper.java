package com.phorloop.tautreminders.controller.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by philliphartin on 30/09/2014.
 */
public class PreferencesHelper {
    public static final String KEY_PREFS_INITIALISED = "initialised";
    public static final String KEY_PREFS_SOME_STRING = "some_string";
    private static final String APP_SHARED_PREFS = PreferencesHelper.class.getSimpleName(); //  Name of the file -.xml

    private SharedPreferences _sharedPrefs;
    private SharedPreferences.Editor _prefsEditor;

    public PreferencesHelper(Context context) {
        this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this._prefsEditor = _sharedPrefs.edit();
    }


    //Get Intialised
    public Boolean getInitialised() {
        return _sharedPrefs.getBoolean(KEY_PREFS_INITIALISED, false); // Get our string from prefs or return an empty string
    }

    public void saveIntialised(Boolean bool) {
        _prefsEditor.putBoolean(KEY_PREFS_INITIALISED, bool);
        _prefsEditor.commit();
    }

    //EXAMPLE
    public String getSomeString() {
        return _sharedPrefs.getString(KEY_PREFS_SOME_STRING, ""); // Get our string from prefs or return an empty string
    }

    public void saveSomeString(String text) {
        _prefsEditor.putString(KEY_PREFS_SOME_STRING, text);
        _prefsEditor.commit();
    }
}
