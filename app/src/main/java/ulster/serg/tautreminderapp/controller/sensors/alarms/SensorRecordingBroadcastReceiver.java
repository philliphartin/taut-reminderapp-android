package ulster.serg.tautreminderapp.controller.sensors.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by philliphartin on 07/11/14.
 */
public class SensorRecordingBroadcastReceiver extends BroadcastReceiver{
    private static final String LOG = "SensorRecordingBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        // Getting reminder object and extras from intent
        Bundle extras = intent.getExtras();
        String reminderID = extras.getString("reminderIdentifier");
        long recordingStartTime = extras.getLong("recordingStartTime");
        long recordingDurationInMs = extras.getLong("recordingDurationInMillis");

        Log.d(LOG, "SensorRecordingBroadcastReceiver fired for reminder: " + reminderID);
//        Log.d(LOG, reminderID + recordingStartTime + recordingDurationInMs);

        //TODO: Launch an ASYNC task to run for the duration in MS polling all sensors
    }
}
