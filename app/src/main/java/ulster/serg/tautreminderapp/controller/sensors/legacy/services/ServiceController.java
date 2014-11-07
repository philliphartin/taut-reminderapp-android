package ulster.serg.tautreminderapp.controller.sensors.legacy.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ulster.serg.tautreminderapp.controller.sensors.legacy.sensor.location.GpsLoggingService;

/**
 * Created by philliphartin on 29/12/2013.
 */
public class ServiceController {

    final static String LOG = "ServiceController";
    final long minute_in_milliseconds = 60 * 1000; //1 Minute = 60000 milliseconds
//    final long reminderInterval = minute_in_milliseconds / 2; // 30 seconds
//    final long windowInterval = minute_in_milliseconds / 6; // 3 seconds
    final long hour_in_milliseconds = 60 * 60 * 1 * 1000;
//    final long syncInterval = hour_in_milliseconds * 1; // 1 Hour


    public void startLoggingServices(Context context) {
        startGPSLoggerService(context);
        startSensorLoggerService(context);
    }

    public void stopLoggingServices(Context context) {
        stopGPSLoggerService(context);
        stopSensorLoggerService(context);
    }

    // Start the GPS logging service
    private void startGPSLoggerService(Context context) {
        Intent gpsLoggerService = new Intent(context, GpsLoggingService.class);
        gpsLoggerService.putExtra("immediate", true);
        context.startService(gpsLoggerService);
    }

    // Stop the GPS logging service
    private void stopGPSLoggerService(Context context) {
        Intent gpsLoggerService = new Intent(context, GpsLoggingService.class);
        gpsLoggerService.putExtra("immediatestop", true);
        context.startService(gpsLoggerService);
        Log.d(LOG, "Stopped GPS Logger Service");
    }

    // Start the Movement logging service
    private void startSensorLoggerService(Context context) {
        context.startService(new Intent(context, ServiceEmbeddedSensorLogger.class));
    }

    // Stop the Movement logging service
    private void stopSensorLoggerService(Context context) {
        context.stopService(new Intent(context, ServiceEmbeddedSensorLogger.class));
        Log.d(LOG, "Stopped Sensor Logger Service");

    }

    ;


}
