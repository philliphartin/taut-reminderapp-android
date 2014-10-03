package com.phorloop.tautreminderapp.controller.helpers;

import android.content.Context;

import com.google.gson.Gson;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.phorloop.tautreminderapp.model.gson.AcknowledgementArrayGSON;
import com.phorloop.tautreminderapp.model.gson.AcknowledgementGSON;
import com.phorloop.tautreminderapp.model.sugarorm.Acknowledgement;
import com.phorloop.tautreminderapp.model.sugarorm.Reminder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by philliphartin on 25/09/2014.
 */
public class AcknowledgementHelper {
    private final Context mContext;
    private final PreferencesHelper preferencesHelper;

    public AcknowledgementHelper(Context context) {
        this.mContext = context;
        preferencesHelper = new PreferencesHelper(mContext);
    }

    public void saveAcknowledgmentLogforReminder(Reminder reminder, Acknowledgement acknowledgement, boolean acknowledged) {

        if (acknowledged) {
            logReminderAsAcknowledged(reminder, acknowledgement);
        } else {
            logReminderAsMissed(reminder);
        }
    }

    public void logReminderAsAcknowledged(Reminder reminder, Acknowledgement acknowledgement) {

        boolean acknowledged = true;
        acknowledgement.setReminderid(reminder.getId());
        acknowledgement.setPatientid(preferencesHelper.getUserId());
        acknowledgement.setAcknowledgedbyuser(convertBooleanToInt(acknowledged));
        acknowledgement.setSenttoserver(0);
        acknowledgement.save();
    }

    public void updateAcknowledgementsAsSentToServer(List<Acknowledgement> acknowledgementList) {
        //For each acknowledgement update as sent to server and save.
        for (int i = 0; i < acknowledgementList.size(); i++) {
            //get acknowledgement and convert to GSON.
            Acknowledgement acknowledgement = acknowledgementList.get(i);
            acknowledgement.setSenttoserver(1);
            acknowledgement.save();
        }
    }

    public void logReminderAsMissed(Reminder reminder) {
        boolean acknowledged = false;

        Acknowledgement acknowledgement = new Acknowledgement();
        acknowledgement.setReminderid(reminder.getId());
        acknowledgement.setPatientid(preferencesHelper.getUserId());
        acknowledgement.setAcknowledgedbyuser(convertBooleanToInt(acknowledged));
        acknowledgement.setTimetoacknowledge(0);
        acknowledgement.setBatterylevel(0);
        acknowledgement.setListencount(0);
        acknowledgement.setSenttoserver(0);

        acknowledgement.save();
    }


    public List<Acknowledgement> getListUnsentAcknowledgements() {
        List<Acknowledgement> list = Select.from(Acknowledgement.class)
                .where(Condition.prop("senttoserver").eq(0)).list();
        return list;
    }

    //Converts List Of basic Acknowledgement objects into an ArrayList of GSON Acknowledgement objects
    private ArrayList getArrayListOfUnsentAcknowledgementsGSON(List<Acknowledgement> acknowledgementList) {
        ArrayList<AcknowledgementGSON> acknowledgementGSONArray = new ArrayList<AcknowledgementGSON>();

        //For each acknowledgement convert to GSON model version
        for (int i = 0; i < acknowledgementList.size(); i++) {
            //get acknowledgement and convert to GSON.
            Acknowledgement acknowledgement = acknowledgementList.get(i);
            AcknowledgementGSON acknowledgementGSON = new AcknowledgementGSON(acknowledgement);
            acknowledgementGSONArray.add(acknowledgementGSON); //Add to GSON array
        }
        return acknowledgementGSONArray;
    }


    public String getUnsentAcknowledgementsAsGSON(List<Acknowledgement> acknowledgementsList) {
        AcknowledgementArrayGSON acknowledgementArrayGSON = new AcknowledgementArrayGSON();
        acknowledgementArrayGSON.setAcknowledgements(getArrayListOfUnsentAcknowledgementsGSON(acknowledgementsList));

        Gson gson = new Gson();
        String gsonToPost = gson.toJson(acknowledgementArrayGSON);
        return gsonToPost;
    }

    public boolean unSentAcknowledgementsAvailable() {
        if (getListUnsentAcknowledgements().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private int convertBooleanToInt(Boolean bool) {
        return bool ? 1 : 0;
    }

}
