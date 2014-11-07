package ulster.serg.tautreminderapp.controller.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import ulster.serg.tautreminderapp.controller.helpers.ReminderHelper;
import ulster.serg.tautreminderapp.controller.sensors.beta.tasks.SenseFromAllPullSensorsTask;
import ulster.serg.tautreminderapp.controller.sensors.legacy.sensor.location.common.Session;
import ulster.serg.tautreminderapp.controller.sensors.legacy.services.ServiceSensorRecordingWindow;

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

        //registerPullSensorRecorder(context, intent);
        registerLegacyRecorder(context, extras);
    }

    private void registerPullSensorRecorder(Context context, Intent intent) {
        SenseFromAllPullSensorsTask senseFromAllPullSensorsTask = new SenseFromAllPullSensorsTask(context, intent);
        senseFromAllPullSensorsTask.execute();
    }

    private void registerLegacyRecorder(Context context, Bundle extras) {
        //Legacy Records use an adapted version of the old sensor recording code
        String reminderIDString = extras.getString("reminderIdentifier");
        Session.setReminderUnixTime(getReminderUnixTime(reminderIDString, context));

        Intent intentSensorRecordingWindowSetup = new Intent(context, ServiceSensorRecordingWindow.class);
        intentSensorRecordingWindowSetup.putExtras(extras);
        context.startService(intentSensorRecordingWindowSetup);
    }

    private long getReminderUnixTime(String reminderIdString, Context context) {
        int reminderId = Integer.parseInt(reminderIdString);
        ReminderHelper reminderHelper = new ReminderHelper(context);
        return reminderHelper.getReminderUnixTimeFromId(reminderId);
    }
}
