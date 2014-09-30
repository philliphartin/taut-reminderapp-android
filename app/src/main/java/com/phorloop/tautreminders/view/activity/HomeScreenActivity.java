package com.phorloop.tautreminders.view.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.phorloop.tautreminders.R;
import com.phorloop.tautreminders.controller.helpers.DateHelper;
import com.phorloop.tautreminders.controller.helpers.PreferencesHelper;
import com.phorloop.tautreminders.view.dialog.WelcomeDialog;


public class HomeScreenActivity extends Activity {

    private static final String LOGa = "HomeScreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_sceen);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new HomeScreenFragment())
                    .commit();
        }

        ActionBar actionBar = getActionBar();
        actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle("TAUT Reminders App");

        //Check if the app has been setup
        PreferencesHelper preferencesHelper = new PreferencesHelper(this);
        boolean initialised = preferencesHelper.getInitialised();

        if (!initialised) {
            WelcomeDialog welcomeDialog = new WelcomeDialog(this);
            welcomeDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_sceen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent_preferences = new Intent(this, SettingsActivity.class);
            startActivity(intent_preferences);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class HomeScreenFragment extends Fragment {

        private static final String LOGf = "FormatChoiceFragment";

        public HomeScreenFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment

            View rootView = inflater.inflate(R.layout.fragment_home_sceen, container, false);

            //Init UI elements
            TextView textView_todaysDate = (TextView) rootView.findViewById(R.id.textView);
            Button button_newReminder = (Button) rootView.findViewById(R.id.button_createReminder);
            Button button_viewReminders = (Button) rootView.findViewById(R.id.button_viewReminders);

            textView_todaysDate.setText("Today's date is " + getTodaysDate());

            button_newReminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(LOGf, "New Reminder button was clicked");
                    Intent intent = new Intent(getActivity(), CreateNewReminderActivity.class);
                    startActivity(intent);
                }
            });

            button_viewReminders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(LOGf, "View Reminder button was clicked");
                    Intent intent = new Intent(getActivity(), ViewRemindersListActivity.class);
                    startActivity(intent);
                }
            });

            return rootView;
        }

        private String getTodaysDate() {
            DateHelper dateHelper = new DateHelper();
            return dateHelper.getDateHumanReadableFromUnixTime(dateHelper.getUnixTimeNow());
        }
    }

    @Override
    public void onBackPressed() {

        //Todo: Create toast that says press back again to exit.
        //Todo: Implement back press counter.

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Do your coding here. For Positive button
                                finish();
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


}
