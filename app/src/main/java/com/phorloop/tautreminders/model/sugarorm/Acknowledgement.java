package com.phorloop.tautreminders.model.sugarorm;

import com.orm.SugarRecord;

/**
 * Created by philliphartin on 24/09/2014.
 */
public class Acknowledgement extends SugarRecord<Acknowledgement> {

    //UserDetails
    int patientid;

    //ReminderDetails
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

    //Statistics
    int timeToAcknowledge;
    int batteryLevel;
    int listenCount;
    int sentToServer;

    public Acknowledgement() {
    }

    public Acknowledgement(int patientid, String format, String time, String date, long unixtime, String dayofweek, String type, String description, String repeatfreq, String createdby, int createdbyid, long audioDuration, int timeToAcknowledge, int batteryLevel, int listenCount, int sentToServer) {
        this.patientid = patientid;
        this.format = format;
        this.time = time;
        this.date = date;
        this.unixtime = unixtime;
        this.dayofweek = dayofweek;
        this.type = type;
        this.description = description;
        this.repeatfreq = repeatfreq;
        this.createdby = createdby;
        this.createdbyid = createdbyid;
        this.audioDuration = audioDuration;
        this.timeToAcknowledge = timeToAcknowledge;
        this.batteryLevel = batteryLevel;
        this.listenCount = listenCount;
        this.sentToServer = sentToServer;
    }

    public int getPatientid() {
        return patientid;
    }

    public void setPatientid(int patientid) {
        this.patientid = patientid;
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

    public int getTimeToAcknowledge() {
        return timeToAcknowledge;
    }

    public void setTimeToAcknowledge(int timeToAcknowledge) {
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

    public int getSentToServer() {
        return sentToServer;
    }

    public void setSentToServer(int sentToServer) {
        this.sentToServer = sentToServer;
    }
}