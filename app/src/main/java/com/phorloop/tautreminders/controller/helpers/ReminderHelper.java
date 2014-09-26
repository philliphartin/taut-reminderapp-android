package com.phorloop.tautreminders.controller.helpers;

import android.content.Context;
import android.util.Log;

import com.orm.query.Select;
import com.phorloop.tautreminders.model.sugarorm.Reminder;

import java.util.Collections;
import java.util.List;

/**
 * Created by philliphartin on 22/09/2014.
 */

public class ReminderHelper {
    private static final String LOG = "ReminderHelper";
    Context mContext;

    public ReminderHelper(Context context){
        this.mContext = context;
    }

    public Reminder getNextReminder() {
        List<Reminder> reminders = Select.from(Reminder.class).where("active = 1").orderBy("unixtime").limit("1").list();

        if (reminders.isEmpty()) {
            return null;
        } else {
            return reminders.get(0);

        }
    }

    public List getActiveReminders() {
        List<Reminder> reminders = Select.from(Reminder.class).where("active = 1").orderBy("unixtime").list();

        if (reminders.isEmpty()) {
            return Collections.emptyList();
        } else {
            return reminders;
        }
    }

    public void softDeleteReminderWithId(long id){
        Reminder reminder = Reminder.findById(Reminder.class, id);
        reminder.setActive(0);
        reminder.save();

        ScheduleHelper scheduleHelper = new ScheduleHelper(mContext);
        scheduleHelper.unScheduleReminder(reminder.getId());
    }

    public void deleteReminderWithId(long id) {
        Reminder reminder = Reminder.findById(Reminder.class, id);
        reminder.delete();
        Log.d(LOG, "Reminder: " + id + " deleted using ReminderHelper");

    }

    public void saveNewReminder(Reminder reminderToSave){
        Reminder reminder = new Reminder(reminderToSave);
        reminder.save();

        ScheduleHelper scheduleHelper = new ScheduleHelper(mContext);
        scheduleHelper.scheduleReminder(reminder);
    }
}