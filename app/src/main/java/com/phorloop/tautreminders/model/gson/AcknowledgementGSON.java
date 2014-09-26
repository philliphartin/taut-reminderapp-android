package com.phorloop.tautreminders.model.gson;

import com.phorloop.tautreminders.model.sugarorm.Acknowledgement;
import com.phorloop.tautreminders.model.sugarorm.Reminder;

/**
 * Created by philliphartin on 25/09/2014.
 */

//GSON Class to create single Acknowledgement logs for transmission.
public class AcknowledgementGSON {

    //ReminderData
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
    long audioDuration;

    //AcknowledgementData
    int patientId;
    int acknowledgedByUser;
    long timeToAcknowledge;
    int batteryLevel;
    int listenCount;

    public AcknowledgementGSON(long acknowledgementId) {

        //Get acknowledgment object
        Acknowledgement acknowledgement = Acknowledgement.findById(Acknowledgement.class, acknowledgementId);
        //Get reminder object for acknowledgement
        Reminder reminder = Reminder.findById(Reminder.class, acknowledgement.getReminderId());

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
        this.audioDuration = reminder.getAudioDuration();
        this.patientId = acknowledgement.getPatientId();
        this.acknowledgedByUser = acknowledgement.getAcknowledgedByUser();
        this.timeToAcknowledge = acknowledgement.getTimeToAcknowledge();
        this.batteryLevel = acknowledgement.getBatteryLevel();
        this.listenCount = acknowledgement.getListenCount();
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

    public long getAudioDuration() {
        return audioDuration;
    }

    public void setAudioDuration(long audioDuration) {
        this.audioDuration = audioDuration;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getAcknowledgedByUser() {
        return acknowledgedByUser;
    }

    public void setAcknowledgedByUser(int acknowledgedByUser) {
        this.acknowledgedByUser = acknowledgedByUser;
    }

    public long getTimeToAcknowledge() {
        return timeToAcknowledge;
    }

    public void setTimeToAcknowledge(long timeToAcknowledge) {
        this.timeToAcknowledge = timeToAcknowledge;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public int getListenCount() {
        return listenCount;
    }

    public void setListenCount(int listenCount) {
        this.listenCount = listenCount;
    }
}
