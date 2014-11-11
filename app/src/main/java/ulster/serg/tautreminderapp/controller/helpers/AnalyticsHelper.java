package ulster.serg.tautreminderapp.controller.helpers;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import ulster.serg.tautreminderapp.controller.MyApplication;
import ulster.serg.tautreminderapp.model.sugarorm.Reminder;

/**
 * Created by philliphartin on 11/11/14.
 */
public class AnalyticsHelper {

    private static final String LOG = "AnalyticsHelper";

    //Analytics
    MixpanelAPI m;
    Tracker g;

    //Activity Passed
    Activity activity;

    public AnalyticsHelper(Activity activity) {
        this.activity = activity;
        initTrackers();
    }

    public void setupTrackerDetails() {

        //Get Parse Object IDs for User tracking
        PreferencesHelper preferencesHelper = new PreferencesHelper(activity);
        boolean initialised = preferencesHelper.getInitialised();
        if (initialised) {

            // Get User ID
            String userid = preferencesHelper.getUserIdAsString();
            // Managing User Identity
            m.identify(userid);
            g.set("&uid", userid);
            // Setting profile properties
            m.getPeople().identify(userid);
            // Sets user's attribute
            m.getPeople().set("name", userid);
        }

    }

    private void initTrackers() {
        try {
            g = ((MyApplication) activity.getApplication()).getGoogleAnalyticsTracker(MyApplication.TrackerName.APP_TRACKER);
            m = ((MyApplication) activity.getApplication()).getMixPanelAnalyticsTracker(MyApplication.TrackerName.APP_TRACKER);
            Log.d("Analytics", "Trackers Initialised");
        } catch (Exception e) {
            Log.d("Analytics", "Failed to Initialise trackers:" + e);
        }
    }


    public void track_appLaunched() {
        JSONObject props = new JSONObject();
        try {
            props.put("Hour Of Day", Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
            props.put("Day Of Week", Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        } catch (JSONException e) {
            Log.e(LOG, e.toString());
        }

        track_appLaunchedIncrementCount();
        m.track("Launched App", props);
    }

    private void track_appLaunchedIncrementCount() {
        Map<String, Integer> properties = new HashMap<String, Integer>();
        properties.put("Launched count", 1);
        m.getPeople().increment(properties);
    }

    public void track_screenView(String screenName) {

        String label = "Viewed Screen: " + screenName;

        //Google
        g.setScreenName(screenName);
        g.send(new HitBuilders.AppViewBuilder().build());

        //MixPanel Analytics
        JSONObject props = new JSONObject();
        try {
            props.put("Hour Of Day", Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
            props.put("Day Of Week", Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        } catch (JSONException e) {
            Log.e(LOG, e.toString());
        }

        m.track(label, props);
        Log.d(LOG, label);
    }

    public void track_PopUpAcknowledged(boolean acknowledged, long timeToAcknowledge) {

        String label = "Reminder Delivered";
        String acknowledgedString = String.valueOf(acknowledged);

        //Google Analytics
        g.send(new HitBuilders.TimingBuilder()
                .setCategory("acknowledgements")
                .setValue(timeToAcknowledge)
                .setLabel(acknowledgedString)
                .build());

        //MixPanel Analytics
        JSONObject props = new JSONObject();
        try {
            props.put("Acknowledged", acknowledgedString);
            props.put("Time To Acknowledge", timeToAcknowledge);
        } catch (JSONException e) {
            Log.e(LOG, e.toString());
        }
        m.track(label, props);

    }

    public void track_reminderTimeSelected(String logType, int hourSelected) {
        String label = "Reminder Time Change: Daily " + logType;

        //MixPanel Analytics
        JSONObject props = new JSONObject();
        try {
            props.put("Hour Selected", hourSelected);
        } catch (JSONException e) {
            Log.e(LOG, e.toString());
        }
        m.track(label, props);
    }

    public void track_reminderDaySelected(String logType, int hourSelected) {
        String label = "Reminder Time Change: Daily " + logType;

        //MixPanel Analytics
        JSONObject props = new JSONObject();
        try {
            props.put("Hour Selected", hourSelected);
        } catch (JSONException e) {
            Log.e(LOG, e.toString());
        }
        m.track(label, props);
    }

    public void track_saveReminder(Reminder reminder) {
        String label = "User Created Reminder";

        //Google Analytics
        g.send(new HitBuilders.EventBuilder()
                .setCategory("action_ui")
                .setLabel(label)
                .build());

        //MixPanel Analytics
        JSONObject props = new JSONObject();
        try {
            props.put("format", reminder.getFormat());
            props.put("time", reminder.getTime());
            props.put("date", reminder.getDate());
            props.put("unixtime", reminder.getUnixtime());
            props.put("dayofweek", reminder.getDayofweek());
            props.put("type", reminder.getType());
            props.put("description", reminder.getDescription());
            props.put("repeatfreq", reminder.getRepeatfreq());
            props.put("createdby", reminder.getCreatedby());
            props.put("createdbyid", reminder.getCreatedbyid());
            props.put("audioduration", reminder.getAudioduration());
        } catch (JSONException e) {
            Log.e(LOG, e.toString());
        }
        m.track(label, props);
    }

    public void track_deleteReminder(String reminderType) {
        String label = "User Deleted Reminder";

        //Google Analytics
        g.send(new HitBuilders.EventBuilder()
                .setCategory("action_ui")
                .setLabel(label)
                .build());

        //MixPanel Analytics
        JSONObject props = new JSONObject();
        try {
            props.put("ReminderType", reminderType);
        } catch (JSONException e) {
            Log.e(LOG, e.toString());
        }
        m.track(label, props);
    }

    public void flush() {
        m.flush();
    }

}
