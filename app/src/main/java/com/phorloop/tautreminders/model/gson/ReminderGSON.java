package com.phorloop.tautreminders.model.gson;

import com.phorloop.tautreminders.model.sugarorm.Reminder;

/**
 * Created by philliphartin on 23/09/2014.
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

    public ReminderGSON(Reminder reminder) {
        this.format = reminder.getFormat();
        this.time = reminder.getTime();
        this.date = reminder.getDate();
        this.unixtime = reminder.getUnixtime();
        this.dayofweek = reminder.getDayofweek();
        this.type = reminder.getType();
        this.description = reminder.getDescription();
        this.repeatfreq = reminder.getRepeatfreq();
        this.createdby = reminder.getCreatedby();
        this.active = reminder.getActive();
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getUnixtime() {
        return unixtime;
    }

    public void setUnixtime(long unixtime) {
        this.unixtime = unixtime;
    }

    public String getDayofweek() {
        return dayofweek;
    }

    public void setDayofweek(String dayofweek) {
        this.dayofweek = dayofweek;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRepeatfreq() {
        return repeatfreq;
    }

    public void setRepeatfreq(String repeatfreq) {
        this.repeatfreq = repeatfreq;
    }

    public String getCreatedby() {
        return createdby;
    }

    public void setCreatedby(String createdby) {
        this.createdby = createdby;
    }

    public int getCreatedbyid() {
        return createdbyid;
    }

    public void setCreatedbyid(int createdbyid) {
        this.createdbyid = createdbyid;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }
}



