package com.phorloop.tautreminders.controller.schedule;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

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

        //Serialise reminder object and add to bundle for intent
        ReminderHelper reminderHelper = new ReminderHelper(mContext);
        String reminderJSON = reminderHelper.reminderAsJSON(reminder);
        Bundle bundle = new Bundle();
        bundle.putString("reminder", reminderJSON);

        Intent intent = new Intent(mContext, ReminderBroadcastReceiver.class);
        intent.putExtras(bundle);

        //Set Alarm for time
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, reminderId, intent, PendingIntent.FLAG_UPDATE_CURRENT); //Pending intent (context, requestCode: same as reminderId, intent, flags)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, unixTime, pendingIntent);

        Log.d(LOG, "Reminder: " + reminderId + " scheduled using ScheduleHelper");
    }

    public void unScheduleReminder(int reminderId) {
        Intent cancelServiceIntent = new Intent(mContext, ReminderBroadcastReceiver.class);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        PendingIntent cancelIntent = PendingIntent.getBroadcast(mContext, reminderId, cancelServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.d(LOG, "Reminder: " + reminderId + " unScheduled using ScheduleHelper");
        alarmManager.cancel(cancelIntent);
    }

    public void rescheduleReminder(Reminder reminder) {

        //Create Joda Instant (from reminder unixtime)
        long unixTime = reminder.getUnixtime();
        Instant instantReminder = new Instant(unixTime);
        DateTime dateTimeReminderOLD = instantReminder.toDateTime();

        Log.d(LOG, "DateTimeReminderOLD" + " Y:" + dateTimeReminderOLD.getYear()
                        + " M:" + dateTimeReminderOLD.getMonthOfYear()
                        + " D:" + dateTimeReminderOLD.getDayOfMonth()
                        + " H:" + dateTimeReminderOLD.getHourOfDay()
                        + " M:" + dateTimeReminderOLD.getMinuteOfHour());

        //Determine what the repeatType is
        if (reminder.getRepeatfreq().contains("Weekly")) {
            DateTime DateTimeReminderNEW = addDaysAndCheck(dateTimeReminderOLD, 7);


            //TODO: Get details for weekly repeat
        } else if (reminder.getRepeatfreq().contains("Everyday")) {
            //TODO: Get details for daily repeat
            DateTime DateTimeReminderNEW = addDaysAndCheck(dateTimeReminderOLD, 1);

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
            dateTime.plusDays(daysToAdd);
            Log.d(LOG, "Iterating potential dates" + " Y:" + dateTime.getYear()
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
