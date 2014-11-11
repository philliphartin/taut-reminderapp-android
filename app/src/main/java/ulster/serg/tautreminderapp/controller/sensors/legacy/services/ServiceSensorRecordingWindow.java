package ulster.serg.tautreminderapp.controller.sensors.legacy.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import ulster.serg.tautreminderapp.controller.MyApplication;

/**
 * Created by philliphartin on 10/01/2014.
 */
public class ServiceSensorRecordingWindow extends Service {

    private static final String LOG = "ServiceSensorRecordingWindow";
    public boolean isRunning = false;
    private ServiceController serviceController;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG, "Service Created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w(LOG, "Service Destroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle extras = intent.getExtras();
        long reminderRecordingWindowTotal = extras.getLong("reminderRecordingWindowTotal");

        Log.d(LOG, "Checking if running: " + isRunning);
        if (isRunning) {
            Log.d(LOG, "Recorder running");
        }

        if (!isRunning) {
            serviceController = new ServiceController();
            Log.i(LOG, "Ready to record");

            //TODO: Setup time
            senseWithParameters(reminderRecordingWindowTotal);
        }

        return super.onStartCommand(intent, flags, startId);
    }


    private void startServices() {
        serviceController.startLoggingServices(MyApplication.getContext());
    }

    private void stopServices() {
        serviceController.stopLoggingServices(MyApplication.getContext());
    }


    protected void senseWithParameters(final long duration) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Log.d(LOG, "Starting sensing");
                Log.d(LOG, "Subscribing for " + duration + " milliseconds");

                long waited = 0;
                try {
                    startServices();

                    while (waited < duration) {
                        Thread.sleep(100);
                        //Log.e(LOG, "Waited for " + waited + " milliseconds");
                        waited += 100;
                    }

                    stopServices();
                    Thread.sleep(1000L);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                stopServices();
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


}
