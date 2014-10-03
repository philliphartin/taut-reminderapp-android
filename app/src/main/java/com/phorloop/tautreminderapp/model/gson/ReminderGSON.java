package com.phorloop.tautreminderapp.model.gson;

import com.phorloop.tautreminderapp.model.sugarorm.Reminder;

/**
 * Created by philliphartin on 02/10/2014.
 */
public class ReminderGSON {

    String format;
    String time;
    String date;
    long unixtime;
    String dayofweek;
    String type;
    String description;
    String repeatfreq;
    String createdby;
    int createdbyid;
    int active;
    String audiofilepath;
    long audioduration;

    public ReminderGSON() {
    }

    public ReminderGSON(long reminderId) {

        Reminder reminder = Reminder.findById(Reminder.class, reminderId);

        this.format = reminder.getFormat();
        this.time = reminder.getTime();
        this.date = reminder.getDate();
        this.unixtime = reminder.getUnixtime();
        this.dayofweek = reminder.getDayofweek();
        this.type = reminder.getType();
        this.description = reminder.getDescription();
        this.repeatfreq = reminder.getRepeatfreq();
        this.createdby = reminder.getCreatedby();
        this.createdbyid = reminder.getCreatedbyid();
        this.active = reminder.getActive();
        this.audiofilepath = reminder.getAudiofilepath();
        this.audioduration = reminder.getAudioduration();
    }

}
