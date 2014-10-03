package com.phorloop.tautreminders.controller.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by philliphartin on 30/09/2014.
 */

public class PreferencesHelper {
    //Shared Preferences
    public static final String KEY_PREFS_INITIALISED = "initialised";
    //Default Preferences
    public static final String DEFAULT_STRING = "";
    public static final String KEY_PREFS_USERNAME = "user_name";
    public static final String KEY_PREFS_USERID = "user_id";
    public static final String KEY_PREFS_CAREREXIST1 = "carer_exist";
    public static final String KEY_PREFS_CAREREXIST2 = "carer_exist2";
    public static final String KEY_PREFS_CARERID1 = "carer_id";
    public static final String KEY_PREFS_CARERID2 = "carer_id2";
    public static final String KEY_PREFS_CARERNAME1 = "carer_name";
    public static final String KEY_PREFS_CARERNAME2 = "carer_name2";
    public static final String KEY_PREFS_SERVERURL = "database";


    //Init SharedPreferences
    private static final String APP_SHARED_PREFS = PreferencesHelper.class.getSimpleName(); //  Name of the file -.xml

    private SharedPreferences _defaultPrefs;
    private SharedPreferences _sharedPrefs;
    private SharedPreferences.Editor _defaultPrefsEditor;
    private SharedPreferences.Editor _sharedPrefsEditor;

    public PreferencesHelper(Context context) {

        this._defaultPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this._defaultPrefsEditor = _defaultPrefs.edit();
        this._sharedPrefsEditor = _sharedPrefs.edit();
    }

    //Get Intialised
    public Boolean getInitialised() {
        return _sharedPrefs.getBoolean(KEY_PREFS_INITIALISED, false); // Get our string from prefs or return an empty string
    }

    public void saveIntialised(Boolean bool) {
        _sharedPrefsEditor.putBoolean(KEY_PREFS_INITIALISED, bool);
        _sharedPrefsEditor.commit();
    }

    public String getUserName() {
        return _defaultPrefs.getString(KEY_PREFS_USERNAME, DEFAULT_STRING);
    }

    public String getServerURL() {
        return _defaultPrefs.getString(KEY_PREFS_SERVERURL, DEFAULT_STRING);
    }

    public int getUserId() {
        return Integer.parseInt(_defaultPrefs.getString(KEY_PREFS_USERID, DEFAULT_STRING));
    }

    public String getUserIdAsString() {
        return _defaultPrefs.getString(KEY_PREFS_USERID, DEFAULT_STRING);
    }

    public String getCarerName1() {
        return _defaultPrefs.getString(KEY_PREFS_CARERNAME1, DEFAULT_STRING);
    }

    public String getCarerName2() {
        return _defaultPrefs.getString(KEY_PREFS_CARERNAME2, DEFAULT_STRING);
    }

    public String getCarerId1() {
        return _defaultPrefs.getString(KEY_PREFS_CARERID1, DEFAULT_STRING);
    }

    public String getCarerId2() {
        return _defaultPrefs.getString(KEY_PREFS_CARERID2, DEFAULT_STRING);
    }

    public Boolean getExistCarer1() {
        return _defaultPrefs.getBoolean(KEY_PREFS_CAREREXIST1, false);
    }

    public Boolean getExistCarer2() {
        return _defaultPrefs.getBoolean(KEY_PREFS_CAREREXIST2, false);
    }

    public Boolean anyCarerEnabled() {
        if (getExistCarer1() || getExistCarer2()) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<Map<String, String>> getCarerHashMap() {

        ArrayList<Map<String, String>> mylist = new ArrayList<Map<String, String>>();

        if (getExistCarer1()) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("carername", getCarerName1());
            map.put("carerid", getCarerId1());

            mylist.add(map);
        }

        if (getExistCarer2()) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("carername", getCarerName2());
            map.put("carerid", getCarerId2());

            mylist.add(map); //FIXME: Crash if only one carer has been selected
        }

        return mylist;
    }


    //EXAMPLE
//    public String getSomeString() {
//        return _sharedPrefs.getString(KEY_PREFS_SOME_STRING, ""); // Get our string from prefs or return an empty string
//    }
//
//    public void saveSomeString(String text) {
//        _sharedPrefsEditor.putString(KEY_PREFS_SOME_STRING, text);
//        _sharedPrefsEditor.commit();
//    }
}
