package com.phorloop.tautreminders.model.sugarorm;

import android.util.Log;

import com.google.gson.Gson;
import com.orm.SugarRecord;
import com.phorloop.tautreminders.model.gson.AcknowledgementGSON;

/**
 * Created by philliphartin on 24/09/2014.
 */
public class Acknowledgement extends SugarRecord<Acknowledgement> {

    long reminderId;
    int patientId;
    int acknowledgedByUser;
    long timeToAcknowledge;
    int batteryLevel;
    int listenCount;
    int sentToServer;


    public Acknowledgement() {
    }

    public Acknowledgement(long reminderId, int patientId, int acknowledgedByUser, int timeToAcknowledge, int batteryLevel, int listenCount, int sentToServer) {
        this.reminderId = reminderId;
        this.patientId = patientId;
        this.acknowledgedByUser = acknowledgedByUser;
        this.timeToAcknowledge = timeToAcknowledge;
        this.batteryLevel = batteryLevel;
        this.listenCount = listenCount;
        this.sentToServer = sentToServer;
    }

    public long getReminderId() {
        return reminderId;
    }

    public void setReminderId(long reminderId) {
        this.reminderId = reminderId;
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

    public int getSentToServer() {
        return sentToServer;
    }

    public void setSentToServer(int sentToServer) {
        this.sentToServer = sentToServer;
    }

    @Override
    public void save() {
        super.save();

        AcknowledgementGSON acknowledgementGSON = new AcknowledgementGSON(getId());
        Gson gson = new Gson();
        String json = gson.toJson(acknowledgementGSON);

        Log.d("Acknowledgement", json);
    }
}