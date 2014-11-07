package ulster.serg.tautreminderapp.controller.sensors;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.joda.time.DateTime;

import ulster.serg.tautreminderapp.controller.helpers.DateHelper;
import ulster.serg.tautreminderapp.model.sugarorm.Reminder;

/**
 * Created by philliphartin on 07/11/14.
 */
public class SensorRecordingHelper {

    private static final String LOG = "SensorRecordingHelper";
    final private int preReminderRecordingWindow = 2; // Number of minutes to record prior to reminder
    final private int postReminderRecordingWindow = 2; // Number of minutes to record after the reminder
    private Context mContext;

    public SensorRecordingHelper(Context mContext) {
        this.mContext = mContext;
    }

    public void scheduleRecording(Reminder reminder) {

        long unixTimeReminder = reminder.getUnixtime(); // Get time of reminder
        DateTime dateTime = new DateTime(unixTimeReminder); // subtract preRecording Window length
        long unixTimeRecordingStart = (dateTime.minusMinutes(preReminderRecordingWindow)).getMillis(); //Convert to Millis
        //long reminderRecordingWindowTotal = unixTimeRecordingStart + (reminderRecordingWindowTotal * 1000L);
        DateHelper dateHelper = new DateHelper();
        String reminderDate = dateHelper.getDateSaveReadableFromDateTime(dateTime);
        String reminderTime = dateHelper.getTimeSaveReadableFromDateTime(dateTime);

        int reminderId = (int) (long) reminder.getId(); // Get reminderID
        String reminderIdentifier = String.valueOf(reminderId); // Package reminderID as string for bundle

        //Serialise reminder object and add to bundle for intent
        Bundle bundle = new Bundle();
        bundle.putString("reminderIdentifier", reminderIdentifier);
        bundle.putLong("recordingStartTime", unixTimeRecordingStart);
        bundle.putLong("reminderRecordingWindowTotal", getReminderRecordingWindowTotalInMillis());
        bundle.putString("reminderDate", reminderDate);
        bundle.putString("reminderTime", reminderTime);

        Intent intent = new Intent(mContext, SensorRecordingBroadcastReceiver.class);
        intent.putExtras(bundle);

        //Set Alarm for time
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, reminderId, intent, PendingIntent.FLAG_UPDATE_CURRENT); //Pending intent (context, requestCode: same as reminderId, intent, flags)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, unixTimeRecordingStart, pendingIntent);
        Log.d(LOG, "Recording for reminder " + reminderId + " scheduled for " + dateHelper.getTimeHumanReadableFromUnixTime(unixTimeRecordingStart) + " on " + dateHelper.getDateHumanReadableFromUnixTime(unixTimeRecordingStart));
    }

    public void unScheduleRecording(long reminderId) {

        Intent cancelServiceIntent = new Intent(mContext, SensorRecordingBroadcastReceiver.class);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        PendingIntent cancelIntent = PendingIntent.getBroadcast(mContext, (int) reminderId, cancelServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d(LOG, "Recording for reminder " + reminderId + " has been unscheduled");
        alarmManager.cancel(cancelIntent);
    }

    private long getReminderRecordingWindowTotalInMillis() {
        final long oneMinuteMs = 60000L;
        long preWindowMs = preReminderRecordingWindow * oneMinuteMs;
        long postRecordingMs = postReminderRecordingWindow * oneMinuteMs;
        return preWindowMs + postRecordingMs;
    }
}
