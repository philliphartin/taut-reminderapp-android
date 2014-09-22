package com.phorloop.tautreminders.model.sugarorm;

import com.orm.SugarRecord;

/**
 * Created by philliphartin on 16/09/2014.
 */
public class Reminder extends SugarRecord<Reminder> {

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

    public Reminder() {
    }

    public Reminder(String description, String format, String time, String date, long unixtime, String dayofweek, String type, String repeatfreq, String createdby, int createdbyid, int active) {
        this.description = description;
        this.format = format;
        this.time = time;
        this.date = date;
        this.unixtime = unixtime;
        this.dayofweek = dayofweek;
        this.type = type;
        this.repeatfreq = repeatfreq;
        this.createdby = createdby;
        this.createdbyid = createdbyid;
        this.active = active;
    }

    public Reminder(Reminder remindertoClone) {
        this.description = remindertoClone.description;
        this.format = remindertoClone.format;
        this.time = remindertoClone.time;
        this.date = remindertoClone.date;
        this.unixtime = remindertoClone.unixtime;
        this.dayofweek = remindertoClone.dayofweek;
        this.type = remindertoClone.type;
        this.repeatfreq = remindertoClone.repeatfreq;
        this.createdby = remindertoClone.createdby;
        this.createdbyid = remindertoClone.createdbyid;
        this.active = remindertoClone.active;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }
}

