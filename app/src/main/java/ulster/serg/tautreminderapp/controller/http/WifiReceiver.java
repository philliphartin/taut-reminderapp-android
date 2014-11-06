package ulster.serg.tautreminderapp.controller.http;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by philliphartin on 06/11/14.
 */
public class WifiReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            Log.d("WifiReceiver", "Have Wifi Connection");
            if (isConnectingToInternet(netInfo)) {
                RemoteDatabaseClientUsage rDCU = new RemoteDatabaseClientUsage(context);
                rDCU.postAcknowledgementLogs();
            }
        } else
            Log.d("WifiReceiver", "Don't have Wifi Connection");
    }


    public boolean isConnectingToInternet(NetworkInfo networkInfo) {
        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
            Log.d("WifiReceiver", "Connected to internet");
            return true;
        } else {
            Log.d("WifiReceiver", "No connection to internet");
            return false;
        }
    }


}