package com.phorloop.tautreminders.controller.broadcastreciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.phorloop.tautreminders.view.activity.PopUpActivity;

/**
 * Created by philliphartin on 23/09/2014.
 */

public class ReminderBroadcastReceiver extends BroadcastReceiver {
    private static final String LOG = "ReminderReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        // Getting reminder object from intent and passing to popup activity
        Bundle extras = intent.getExtras();
        Log.d(LOG, "ReminderReceiver fired");

        Intent popup = new Intent(context, PopUpActivity.class);
        popup.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        popup.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        popup.putExtras(extras);

        context.startActivity(popup);
    }
}
