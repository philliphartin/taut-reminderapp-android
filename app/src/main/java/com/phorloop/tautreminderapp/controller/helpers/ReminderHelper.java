package com.phorloop.tautreminderapp.controller.helpers;

import android.content.Context;
import android.util.Log;

import com.orm.query.Condition;
import com.orm.query.Select;
import com.phorloop.tautreminderapp.model.sugarorm.Reminder;

import java.util.Collections;
import java.util.List;

/**
 * Created by philliphartin on 22/09/2014.
 */

public class ReminderHelper {
    private static final String LOG = "ReminderHelper";
    Context mContext;

    public ReminderHelper(Context context) {
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

        List<Reminder> reminders =
                Select.from(Reminder.class)
                        .where(Condition.prop("active").eq(1)).orderBy("unixtime")
                        .list(); // Is still active

        if (reminders.isEmpty()) {
            return Collections.emptyList();
        } else {
            return reminders;
        }
    }

    public void softDeleteReminderWithId(long id) {
        Reminder reminder = Reminder.findById(Reminder.class, id);
        softDeleteReminder(reminder);
    }

    public void softDeleteReminder(Reminder reminder) {
        reminder.setActive(0);
        reminder.save();

        ScheduleHelper scheduleHelper = new ScheduleHelper(mContext);
        scheduleHelper.unScheduleReminder(reminder.getId());
    }

    public void deleteReminderWithId(long id) {
        Reminder reminder = Reminder.findById(Reminder.class, id);
        reminder.delete();
        Log.d(LOG, "Reminder: " + id + " deleted");
    }

    public void saveNewReminder(Reminder reminderToSave) {
        Reminder reminder = new Reminder(reminderToSave);
        reminder.save();

        ScheduleHelper scheduleHelper = new ScheduleHelper(mContext);
        scheduleHelper.scheduleReminder(reminder);
    }

    public void processRepeatReminder(Reminder reminder) {
        saveNewReminder(reminder);        //Save new reminder
        softDeleteReminderWithId(reminder.getId()); //Make original non-active (use Id rather than passing object, as pushing object pushes new reminder object details
    }


    public boolean isVoiceReminder(Reminder reminder) {
        if (reminder.getType().equals("voice")) {
            return true;
        } else {
            return false;
        }
    }

    public List<Reminder> getActiveRemindersFromPast(long unixTimeNow) {
        List<Reminder> reminderList = Select.from(Reminder.class)
                .where(Condition.prop("unixtime").lt(unixTimeNow), // Time is in the past
                        (Condition.prop("active").eq(1))).list(); // Is still active
        return reminderList;
    }


//    public boolean isRepeatReminder(Reminder reminder) {
//        if (reminder.getRepeatfreq().contains("Never")) {
//            return false;
//        } else {
//            return true;
//        }
//    }

    //    private void removeAudioFileIfApplicable(Reminder reminder) {
//        if (isVoiceReminder(reminder)) {
//            if (!isRepeatReminder(reminder)) {
//                FileHelper fileHelper = new FileHelper();
//                fileHelper.deleteAudioFileForReminder(reminder);
//            }
//        }
//    }
}
