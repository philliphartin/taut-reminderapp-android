package ulster.serg.tautreminderapp.controller.sensors.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import ulster.serg.tautreminderapp.controller.sensors.tasks.SenseFromAllPullSensorsTask;

/**
 * Created by philliphartin on 07/11/14.
 */
public class SensorRecordingBroadcastReceiver extends BroadcastReceiver {
    private static final String LOG = "SensorRecordingBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        // Getting reminder object and extras from intent
        Bundle extras = intent.getExtras();
        String reminderID = extras.getString("reminderIdentifier");
        Log.d(LOG, "SensorRecordingBroadcastReceiver fired for reminder: " + reminderID);

        SenseFromAllPullSensorsTask senseFromAllPullSensorsTask = new SenseFromAllPullSensorsTask(context, intent);
        senseFromAllPullSensorsTask.execute();

        //TODO: Launch an ASYNC task to run for the duration in MS polling all sensors
    }
}
