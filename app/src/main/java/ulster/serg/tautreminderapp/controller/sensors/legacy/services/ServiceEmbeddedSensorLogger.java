package ulster.serg.tautreminderapp.controller.sensors.legacy.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import ulster.serg.tautreminderapp.controller.sensors.legacy.sensor.movement.SensorRecorder;
import ulster.serg.tautreminderapp.controller.sensors.legacy.sensor.movement.StorageErrorException;


/**
 * Created by philliphartin on 07/01/2014.
 */
public class ServiceEmbeddedSensorLogger extends Service {
    private static final String LOG = "ServiceEmbeddedSensorLogger";
    private SensorRecorder mSensorRecorder;
    //Sensors
    private boolean accelerometer = true;
    private boolean orientation = false;
    private boolean magnetic = true;
    private boolean light = true;
    private boolean ambienttemp = true;
    private boolean proximity = true;
    private boolean gyroscope = true;
    private boolean linearaccleration = false;
    private int samplingRate = 1; //GAME

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG, "Service Created");

        new SensorRecorder(this);
        //createNotification();

        try {
            startLog();
        } catch (SamplingRateNotSelectedException ex) {
            Log.e(LOG, "Sampling rate not selected: " + ex.getMessage());
        } catch (StorageErrorException ex) {
            Log.e(LOG, "StorageErrorException: " + ex.getMessage());
        } catch (NoSensorSelected e) {
            Log.e(LOG, "NoSensorSelected: " + e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLog();
        Log.w(LOG, "Service Destroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return 1;
    }

    private void startLog() throws SamplingRateNotSelectedException, StorageErrorException, NoSensorSelected {
        // Get options for recording from view

        // If no sensor is selected, launch exception
        if (!(accelerometer || orientation || magnetic || light || ambienttemp || proximity || linearaccleration || gyroscope)) {
            throw new NoSensorSelected();
        }

        //Initialise
        mSensorRecorder = new SensorRecorder(this);
        //Start recording
        mSensorRecorder.startRecording(accelerometer, orientation, magnetic, light, ambienttemp, proximity, linearaccleration, gyroscope, samplingRate);
    }

    private void stopLog() {
        if (mSensorRecorder.isRunning())
            mSensorRecorder.stopRecording();
    }

    private class SamplingRateNotSelectedException extends Exception {

        /**
         *
         */
        private static final long serialVersionUID = 2357996218011686837L;
    }

    private class NoSensorSelected extends Exception {

        /**
         *
         */
        private static final long serialVersionUID = -3325254008407183499L;
    }


}
