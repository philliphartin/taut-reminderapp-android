package com.phorloop.tautreminders.controller.helpers;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.phorloop.tautreminders.controller.broadcastreciever.ReminderBroadcastReceiver;
import com.phorloop.tautreminders.model.sugarorm.Reminder;

import org.joda.time.DateTime;
import org.joda.time.Instant;

/**
 * Created by philliphartin on 22/09/2014.
 */
public class ScheduleHelper {

    private static final String LOG = "ScheduleHelper";
    Context mContext;

    public ScheduleHelper(Context context) {
        this.mContext = context;
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

        Log.d(LOG, "Reminder: " + reminderId + " scheduled using ScheduleHelper");
    }

    public void unScheduleReminder(long reminderId) {
        Intent cancelServiceIntent = new Intent(mContext, ReminderBroadcastReceiver.class);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        PendingIntent cancelIntent = PendingIntent.getBroadcast(mContext, (int) reminderId, cancelServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.d(LOG, "Reminder: " + reminderId + " unScheduled using ScheduleHelper");
        alarmManager.cancel(cancelIntent);
    }

    public void rescheduleReminder(Reminder reminder) {
        //Create Joda Instant (from reminder unixtime)
        long unixTime = reminder.getUnixtime();
        Instant instantReminder = new Instant(unixTime);
        DateTime dateTimeReminderOLD = instantReminder.toDateTime();
        DateTime dateTimeReminderNEW = new DateTime();

        Log.d(LOG, "DateTimeReminderOLD" + " Y:" + dateTimeReminderOLD.getYear()
                + " M:" + dateTimeReminderOLD.getMonthOfYear()
                + " D:" + dateTimeReminderOLD.getDayOfMonth()
                + " H:" + dateTimeReminderOLD.getHourOfDay()
                + " M:" + dateTimeReminderOLD.getMinuteOfHour());

        //Determine what the repeatType is
        if (reminder.getRepeatfreq().contains("Weekly")) {
            dateTimeReminderNEW = addDaysAndCheck(dateTimeReminderOLD, 7);
        } else if (reminder.getRepeatfreq().contains("Everyday")) {
            dateTimeReminderNEW = addDaysAndCheck(dateTimeReminderOLD, 1);
        }

        Log.d(LOG, "DateTimeReminderNEW" + " Y:" + dateTimeReminderNEW.getYear()
                + " M:" + dateTimeReminderNEW.getMonthOfYear()
                + " D:" + dateTimeReminderNEW.getDayOfMonth()
                + " H:" + dateTimeReminderNEW.getHourOfDay()
                + " M:" + dateTimeReminderNEW.getMinuteOfHour());

        //Update reminder objects dates
        DateHelper dateHelper = new DateHelper();
        String date = dateHelper.getDateSaveReadableFromDateTime(dateTimeReminderNEW);
        String time = dateHelper.getTimeSaveReadableFromDateTime(dateTimeReminderNEW);

        reminder.setDate(date);
        reminder.setTime(time);
        reminder.setUnixtime(dateTimeReminderNEW.getMillis());
        reminder.setDayofweek(dateTimeReminderNEW.dayOfWeek().getAsText());

        ReminderHelper reminderHelper = new ReminderHelper(mContext);
        reminderHelper.processRepeatReminder(reminder);
    }

    public boolean needsRescheduled(Reminder reminder) {
        if (reminder.getRepeatfreq().contains("Never")) {
            Log.d(LOG, "Repeat not required");
            return false;
        } else {
            Log.d(LOG, "Repeat required");
            return true;
        }
    }

    private static DateTime calcNextDate(DateTime d, int day) {
        if (d.getDayOfWeek() > day) {
            d = d.plusWeeks(1);
        }
        return d.withDayOfWeek(day);
    }

    private DateTime addDaysAndCheck(DateTime dateTime, int daysToAdd) {

        while (dateTime.isBeforeNow()) {
            Log.d(LOG, "dateTime.IsBeforeNow " + dateTime.isBeforeNow());

            dateTime = dateTime.plusDays(daysToAdd);

            Log.d(LOG, "Adding" + daysToAdd + " days to dateObject:" + " Y:" + dateTime.getYear()
                    + " M:" + dateTime.getMonthOfYear()
                    + " D:" + dateTime.getDayOfMonth()
                    + " H:" + dateTime.getHourOfDay()
                    + " M:" + dateTime.getMinuteOfHour());
        }

        Log.d(LOG, "Finalised new date" + " Y:" + dateTime.getYear()
                + " M:" + dateTime.getMonthOfYear()
                + " D:" + dateTime.getDayOfMonth()
                + " H:" + dateTime.getHourOfDay()
                + " M:" + dateTime.getMinuteOfHour());
        return dateTime;
    }
}
