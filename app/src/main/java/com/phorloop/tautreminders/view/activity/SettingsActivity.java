package com.phorloop.tautreminders.view.activity;

import android.app.Activity;
import android.os.Bundle;

import com.phorloop.tautreminders.view.fragment.SettingsFragment;

/**
 * Created by philliphartin on 30/09/2014.
 */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
