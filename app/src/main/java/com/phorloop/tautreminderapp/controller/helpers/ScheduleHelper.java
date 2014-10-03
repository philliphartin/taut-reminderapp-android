package com.phorloop.tautreminderapp.controller.helpers;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.phorloop.tautreminderapp.controller.broadcastreciever.ReminderBroadcastReceiver;
import com.phorloop.tautreminderapp.model.sugarorm.Reminder;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import java.util.List;

/**
 * Created by philliphartin on 22/09/2014.
 */
public class ScheduleHelper {

    private static final String LOG = "ScheduleHelper";
    Context mContext;

    public ScheduleHelper(Context context) {
        this.mContext = context;
    }

    private static DateTime calcNextDate(DateTime d, int day) {
        if (d.getDayOfWeek() > day) {
            d = d.plusWeeks(1);
        }
        return d.withDayOfWeek(day);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT) // For KitKat and above to set exact time
    public void scheduleReminder(Reminder reminder) {

        long unixTime = reminder.getUnixtime(); // Get time of reminder
        int reminderId = (int) (long) reminder.getId();
        String reminderIdentifier = String.valueOf(reminderId);

        //Serialise reminder object and add to bundle for intent
        Bundle bundle = new Bundle();
        bundle.putString("reminderIdentifier", reminderIdentifier);
        Intent intent = new Intent(mContext, ReminderBroadcastReceiver.class);
        intent.putExtras(bundle);

        //Set Alarm for time
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, reminderId, intent, PendingIntent.FLAG_UPDATE_CURRENT); //Pending intent (context, requestCode: same as reminderId, intent, flags)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, unixTime, pendingIntent);

        Log.d(LOG, "Reminder " + reminderId + ": scheduled");
    }

    public void unScheduleReminder(long reminderId) {
        Intent cancelServiceIntent = new Intent(mContext, ReminderBroadcastReceiver.class);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        PendingIntent cancelIntent = PendingIntent.getBroadcast(mContext, (int) reminderId, cancelServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.d(LOG, "Reminder " + reminderId + ": unScheduled");
        alarmManager.cancel(cancelIntent);
    }

    public void rescheduleReminder(Reminder reminder) {

        ReminderHelper reminderHelper = new ReminderHelper(mContext);

        if (needsRescheduled(reminder)) {
            //Get repeatFrequency
            String repeatFreq = reminder.getRepeatfreq();

            //Create Joda Instant
            long unixTime = reminder.getUnixtime();
            Instant instantReminder = new Instant(unixTime);
            DateTime dateTimeReminderOLD = instantReminder.toDateTime();
            DateTime dateTimeReminderNEW = new DateTime();

            logDebugDateTimeDetails(dateTimeReminderOLD, "Old");
            //Determine what the repeatType is
            if (repeatFreq.equals("Weekly")) {
                dateTimeReminderNEW = addDaysAndCheck(dateTimeReminderOLD, 7);
            } else if (repeatFreq.equals("Everyday")) {
                dateTimeReminderNEW = addDaysAndCheck(dateTimeReminderOLD, 1);
            }
            logDebugDateTimeDetails(dateTimeReminderNEW, "New");

            //Init new date properties
            DateHelper dateHelper = new DateHelper();
            String newDate = dateHelper.getDateSaveReadableFromDateTime(dateTimeReminderNEW);
            String newTime = dateHelper.getTimeSaveReadableFromDateTime(dateTimeReminderNEW);
            long newUnixTime = dateTimeReminderNEW.getMillis();
            String newDayOfWeek = dateTimeReminderNEW.dayOfWeek().getAsText();

            //Assign properties to reminder object
            reminder.setDate(newDate);
            reminder.setTime(newTime);
            reminder.setUnixtime(newUnixTime);
            reminder.setDayofweek(newDayOfWeek);
            reminder.setActive(1);

            //Process repeat reminder
            reminderHelper.processRepeatReminder(reminder);
        } else {
            // Repeat Not Needed
            reminderHelper.softDeleteReminder(reminder);
        }
    }

    public boolean needsRescheduled(Reminder reminder) {
        if (reminder.getRepeatfreq().equals("Never")) {
            Log.d(LOG, "Repeat not required");
            return false;
        } else {
            Log.d(LOG, "Repeat required: " + reminder.getRepeatfreq());
            return true;
        }
    }


    private DateTime addDaysAndCheck(DateTime dateTime, int daysToAdd) {

        while (dateTime.isBeforeNow()) {
            dateTime = dateTime.plusDays(daysToAdd);
        }
        return dateTime;
    }

    public void rescheduleMissedReminders() {
        //Helpers
        DateHelper dateHelper = new DateHelper();
        ReminderHelper reminderHelper = new ReminderHelper(mContext);
        AcknowledgementHelper acknowledgementHelper = new AcknowledgementHelper(mContext);

        List<Reminder> reminderList = reminderHelper.getActiveRemindersFromPast(dateHelper.getUnixTimeNow());

        for (int i = 0; i < reminderList.size(); i++) {
            Reminder reminder = reminderList.get(i);

            // If missed reminder needs rescheduled then reschedule
            if (needsRescheduled(reminder)) {
                rescheduleReminder(reminder);
            } else {
                reminderHelper.softDeleteReminder(reminder); //If doesn't need reschudled just make not active
            }
            acknowledgementHelper.logReminderAsMissed(reminder); // Log as missed for all
        }
    }

    private void logDebugDateTimeDetails(DateTime dateTime, String oldOrNew) {
        Log.d(LOG, oldOrNew + " Y:" + dateTime.getYear()
                + " M:" + dateTime.getMonthOfYear()
                + " D:" + dateTime.getDayOfMonth()
                + " H:" + dateTime.getHourOfDay()
                + " M:" + dateTime.getMinuteOfHour());
    }
}
