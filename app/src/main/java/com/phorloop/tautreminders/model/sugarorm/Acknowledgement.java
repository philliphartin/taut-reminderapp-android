package com.phorloop.tautreminders.model.sugarorm;

import android.util.Log;

import com.google.gson.Gson;
import com.orm.SugarRecord;
import com.phorloop.tautreminders.model.gson.AcknowledgementGSON;

/**
 * Created by philliphartin on 24/09/2014.
 */
public class Acknowledgement extends SugarRecord<Acknowledgement> {

    long reminderid;
    int patientid;
    int acknowledgedbyuser;
    long timetoacknowledge;
    int batterylevel;
    int listencount;
    int senttoserver;

    public Acknowledgement() {
    }

    public Acknowledgement(long reminderid, int patientid, int acknowledgedbyuser, long timetoacknowledge, int batterylevel, int listencount, int senttoserver) {
        this.reminderid = reminderid;
        this.patientid = patientid;
        this.acknowledgedbyuser = acknowledgedbyuser;
        this.timetoacknowledge = timetoacknowledge;
        this.batterylevel = batterylevel;
        this.listencount = listencount;
        this.senttoserver = senttoserver;
    }

    public long getReminderid() {
        return reminderid;
    }

    public void setReminderid(long reminderid) {
        this.reminderid = reminderid;
    }

    public int getPatientid() {
        return patientid;
    }

    public void setPatientid(int patientid) {
        this.patientid = patientid;
    }

    public int getAcknowledgedbyuser() {
        return acknowledgedbyuser;
    }

    public void setAcknowledgedbyuser(int acknowledgedbyuser) {
        this.acknowledgedbyuser = acknowledgedbyuser;
    }

    public long getTimetoacknowledge() {
        return timetoacknowledge;
    }

    public void setTimetoacknowledge(long timetoacknowledge) {
        this.timetoacknowledge = timetoacknowledge;
    }

    public int getBatterylevel() {
        return batterylevel;
    }

    public void setBatterylevel(int batterylevel) {
        this.batterylevel = batterylevel;
    }

    public int getListencount() {
        return listencount;
    }

    public void setListencount(int listencount) {
        this.listencount = listencount;
    }

    public int getSenttoserver() {
        return senttoserver;
    }

    public void setSenttoserver(int senttoserver) {
        this.senttoserver = senttoserver;
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