package com.phorloop.tautreminderapp.controller.broadcastreciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.phorloop.tautreminderapp.controller.helpers.ScheduleHelper;

/**
 * Created by philliphartin on 29/09/2014.
 */
public class OnBootChecker extends BroadcastReceiver {
    private final static String LOG = "OnBootChecker";


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d(LOG, "Boot received");
            ScheduleHelper scheduleHelper = new ScheduleHelper(context);
            scheduleHelper.rescheduleMissedReminders();
        }
    }

}