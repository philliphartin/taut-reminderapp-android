package ulster.serg.tautreminderapp.view.activity;

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

import ulster.serg.tautreminderapp.R;
import ulster.serg.tautreminderapp.controller.helpers.AnalyticsHelper;
import ulster.serg.tautreminderapp.controller.helpers.DatabaseImporter;
import ulster.serg.tautreminderapp.controller.helpers.DateHelper;
import ulster.serg.tautreminderapp.controller.helpers.PreferencesHelper;
import ulster.serg.tautreminderapp.controller.http.RemoteDatabaseClientUsage;
import ulster.serg.tautreminderapp.view.dialog.WelcomeDialog;


public class HomeScreenActivity extends Activity {

    private static final String LOGa = "HomeScreenActivity";
    private AnalyticsHelper analytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ulster.serg.tautreminderapp.R.layout.activity_home_sceen);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(ulster.serg.tautreminderapp.R.id.container, new HomeScreenFragment())
                    .commit();
        }

        //Initialise Analytics
        analytics = new AnalyticsHelper(this);

        //Setup UI
        ActionBar actionBar = getActionBar();
        actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle("TAUT Reminders App");

        //Check if the app has been setup with user details
        PreferencesHelper preferencesHelper = new PreferencesHelper(this);
        boolean initialised = preferencesHelper.getInitialised();

        if (!initialised) {
            WelcomeDialog welcomeDialog = new WelcomeDialog(this);
            welcomeDialog.show();
        } else {
            analytics.setupTrackerDetails();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(ulster.serg.tautreminderapp.R.menu.home_sceen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == ulster.serg.tautreminderapp.R.id.action_settings) {
            Intent intent_preferences = new Intent(this, SettingsActivity.class);
            startActivity(intent_preferences);
        } else if (id == ulster.serg.tautreminderapp.R.id.action_upload) {
            RemoteDatabaseClientUsage rDCU = new RemoteDatabaseClientUsage(this);
            rDCU.postAcknowledgementLogs();
        } else if (id == R.id.action_import) {
            DatabaseImporter importer = new DatabaseImporter(this);
            importer.execute("");
//        } else if (id == R.id.action_test) {
//            Intent intent_test = new Intent(this, TestActivity.class);
//            startActivity(intent_test);
        }


        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onResume() {
        super.onResume();
        analytics.track_appLaunched();
    }

    @Override
    protected void onDestroy() {
        analytics.flush();
        super.onDestroy();
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

            View rootView = inflater.inflate(ulster.serg.tautreminderapp.R.layout.fragment_home_sceen, container, false);

            //Init UI elements
            TextView textView_todaysDate = (TextView) rootView.findViewById(ulster.serg.tautreminderapp.R.id.textView);
            Button button_newReminder = (Button) rootView.findViewById(ulster.serg.tautreminderapp.R.id.button_createReminder);
            Button button_viewReminders = (Button) rootView.findViewById(ulster.serg.tautreminderapp.R.id.button_viewReminders);

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
}
