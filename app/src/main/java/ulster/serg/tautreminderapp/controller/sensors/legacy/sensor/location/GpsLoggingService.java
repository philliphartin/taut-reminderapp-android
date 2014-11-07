/*
*    This file is part of GPSLogger for Android.
*
*    GPSLogger for Android is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 2 of the License, or
*    (at your option) any later version.
*
*    GPSLogger for Android is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with GPSLogger for Android.  If not, see <http://www.gnu.org/licenses/>.
*/


package ulster.serg.tautreminderapp.controller.sensors.legacy.sensor.location;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import ulster.serg.tautreminderapp.R;
import ulster.serg.tautreminderapp.controller.MyApplication;
import ulster.serg.tautreminderapp.controller.helpers.PreferencesHelper;
import ulster.serg.tautreminderapp.controller.sensors.legacy.sensor.location.common.AppSettings;
import ulster.serg.tautreminderapp.controller.sensors.legacy.sensor.location.common.IActionListener;
import ulster.serg.tautreminderapp.controller.sensors.legacy.sensor.location.common.Session;
import ulster.serg.tautreminderapp.controller.sensors.legacy.sensor.location.common.Utilities;
import ulster.serg.tautreminderapp.controller.sensors.legacy.sensor.location.loggers.FileLoggerFactory;
import ulster.serg.tautreminderapp.controller.sensors.legacy.sensor.location.loggers.IFileLogger;
import ulster.serg.tautreminderapp.view.activity.HomeScreenActivity;

public class GpsLoggingService extends Service implements IActionListener {

    private static String LOG = "GPSLoggingService";
    private static NotificationManager gpsNotifyManager;
    private static int NOTIFICATION_ID = 8675309;

    private final IBinder mBinder = new GpsLoggingBinder();
    private static IGpsLoggerServiceClient mainServiceClient;
    private boolean forceLogOnce = false;

    // ---------------------------------------------------
    // Helpers and managers
    // ---------------------------------------------------
    private GeneralLocationListener gpsLocationListener;
    private GeneralLocationListener towerLocationListener;
    LocationManager gpsLocationManager;
    private LocationManager towerLocationManager;
    AlarmManager nextPointAlarmManager;

    // ---------------------------------------------------

    @Override
    public IBinder onBind(Intent arg0) {
        Utilities.LogDebug("GpsLoggingService.onBind");
        return mBinder;
    }

    @Override
    public void onCreate() {
        //Utilities.LogDebug("GpsLoggingService.onCreate");
        nextPointAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //Utilities.LogInfo("GPSLoggerService created");
        Log.v(LOG, "Service Created");

    }

    @Override
    public void onStart(Intent intent, int startId) {
        //Utilities.LogDebug("GpsLoggingService.onStart");
        HandleIntent(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Utilities.LogDebug("GpsLoggingService.onStartCommand");
        HandleIntent(intent);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        //Utilities.LogWarning("GpsLoggingService is being destroyed by Android OS.");
        mainServiceClient = null;
        StopLogging();
        Log.v(LOG, "Service Destroyed by Android OS");
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        Utilities.LogWarning("Android is low on memory.");
        super.onLowMemory();
    }

    private void HandleIntent(Intent intent) {
        //Utilities.LogDebug("GpsLoggingService.handleIntent");
        GetPreferences();
        //Utilities.LogDebug("Null intent? " + String.valueOf(intent == null));

        if (intent != null) {
            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                boolean stopRightNow = bundle.getBoolean("immediatestop");
                boolean startRightNow = bundle.getBoolean("immediate");
                boolean getNextPoint = bundle.getBoolean("getnextpoint");

                //Utilities.LogDebug("startRightNow - " + String.valueOf(startRightNow));

                if (startRightNow) {
                    Utilities.LogInfo("Auto starting logging");

                    StartLogging();
                }

                if (stopRightNow) {
                    Utilities.LogInfo("Auto stop logging");
                    StopLogging();
                }

                if (getNextPoint && Session.isStarted()) {
                    Utilities.LogDebug("HandleIntent - getNextPoint");
                    StartGpsManager();
                }

            }
        } else {
            // A null intent is passed in if the service has been killed and
            // restarted.
            //Utilities.LogDebug("Service restarted with null intent. Start logging.");
            StartLogging();

        }
    }

    @Override
    public void OnComplete() {
        Utilities.HideProgress();
    }

    @Override
    public void OnFailure() {
        Utilities.HideProgress();
    }

    /**
     * Can be used from calling classes as the go-between for methods and
     * properties.
     */
    public class GpsLoggingBinder extends Binder {
        public GpsLoggingService getService() {
            //Utilities.LogDebug("GpsLoggingBinder.getService");
            return GpsLoggingService.this;
        }
    }

    private void SetForceLogOnce(boolean flag) {
        forceLogOnce = flag;
    }

    private boolean ForceLogOnce() {
        return forceLogOnce;
    }

    /**
     * Sets the activity form for this service. The activity form needs to
     * implement IGpsLoggerServiceClient.
     *
     * @param mainForm The calling client
     */
    protected static void SetServiceClient(IGpsLoggerServiceClient mainForm) {
        mainServiceClient = mainForm;
    }

    /**
     * Gets preferences chosen by the user and populates the AppSettings object.
     * Also sets up email timers if required.
     */
    private void GetPreferences() {
        //Utilities.LogDebug("GpsLoggingService.GetPreferences");
        Utilities.PopulateAppSettings(getApplicationContext());
        //Utilities.LogDebug("AppSettings.getRecordtoText " + AppSettings.shouldLogToPlainText());
    }

    /**
     * Resets the form, resets file name if required, reobtains preferences
     */
    protected void StartLogging() {
        //Utilities.LogDebug("GpsLoggingService.StartLogging");
        Session.setAddNewTrackSegment(true);

        if (Session.isStarted()) {
            return;
        }

        Utilities.LogInfo("Starting logging procedures");
        try {
            startForeground(NOTIFICATION_ID, new Notification());
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }

        Session.setStarted(true);

        GetPreferences();
        Notify();
        ResetCurrentFileName(true);
        ClearForm();
        StartGpsManager();

    }

    /**
     * Asks the main service client to clear its form.
     */
    private void ClearForm() {
        if (IsMainFormVisible()) {
            mainServiceClient.ClearForm();
        }
    }

    /**
     * Stops logging, removes notification, stops GPS manager, stops email timer
     */
    public void StopLogging() {
        //Utilities.LogDebug("GpsLoggingService.StopLogging");
        Session.setAddNewTrackSegment(true);

        Utilities.LogInfo("Stopping logging");
        Session.setStarted(false);

        Session.setCurrentLocationInfo(null);
        stopForeground(true);

        RemoveNotification();
        StopAlarm();
        StopGpsManager();
        StopMainActivity();
    }

    /**
     * Manages the notification in the status bar
     */
    private void Notify() {

        //Utilities.LogDebug("GpsLoggingService.Notify");
        if (AppSettings.shouldShowInNotificationBar()) {
            gpsNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            ShowNotification();
        } else {
            RemoveNotification();
        }
    }

    /**
     * Hides the notification icon in the status bar if it's visible.
     */
    private void RemoveNotification() {
        //Utilities.LogDebug("GpsLoggingService.RemoveNotification");
        try {
            if (Session.isNotificationVisible()) {
                gpsNotifyManager.cancelAll();
            }
        } catch (Exception ex) {
            Utilities.LogError("RemoveNotification", ex);
        } finally {
            Session.setNotificationVisible(false);
        }
    }

    /**
     * Shows a notification icon in the status bar for GPS Logger
     */
    private void ShowNotification() {
        //Utilities.LogDebug("GpsLoggingService.ShowNotification");
        Intent contentIntent = new Intent(this, HomeScreenActivity.class);
        PendingIntent pending = PendingIntent.getActivity(getApplicationContext(), 0, contentIntent, PendingIntent.FLAG_ONE_SHOT);

        Notification nfc = new Notification(R.drawable.ic_launcher, null, System.currentTimeMillis());
        nfc.flags |= Notification.FLAG_ONGOING_EVENT;

        NumberFormat nf = new DecimalFormat("###.######");

        String contentText = getString(R.string.notification_text);
        if (Session.hasValidLocation()) {
            contentText = nf.format(Session.getCurrentLatitude()) + ","
                    + nf.format(Session.getCurrentLongitude());
        }

        nfc.setLatestEventInfo(getApplicationContext(), getString(R.string.notification_text),
                contentText, pending);

        gpsNotifyManager.notify(NOTIFICATION_ID, nfc);
        Session.setNotificationVisible(true);
    }

    /**
     * Starts the location manager. There are two location managers - GPS and
     * Cell Tower. This code determines which manager to request updates from
     * based on user preference and whichever is enabled. If GPS is enabled on
     * the phone, that is used. But if the user has also specified that they
     * prefer cell towers, then cell towers are used. If neither is enabled,
     * then nothing is requested.
     */
    private void StartGpsManager() {
        Utilities.LogDebug("Starting GpsManager");

        GetPreferences();

        if (gpsLocationListener == null) {
            gpsLocationListener = new GeneralLocationListener(this);
        }

        if (towerLocationListener == null) {
            towerLocationListener = new GeneralLocationListener(this);
        }


        gpsLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        towerLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        CheckTowerAndGpsStatus();

        if (Session.isGpsEnabled() && !AppSettings.shouldPreferCellTower()) {
            Utilities.LogInfo("Requesting GPS location updates");
            // gps satellite based
            gpsLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, 0,
                    gpsLocationListener);

            gpsLocationManager.addGpsStatusListener(gpsLocationListener);

            Session.setUsingGps(true);
        } else if (Session.isTowerEnabled()) {
            Utilities.LogInfo("Requesting tower location updates");
            Session.setUsingGps(false);
            // Cell tower and wifi based
            towerLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1000, 0,
                    towerLocationListener);

        } else {
            Utilities.LogInfo("No provider available");
            Session.setUsingGps(false);
            SetStatus(R.string.gpsprovider_unavailable);
            SetFatalMessage(R.string.gpsprovider_unavailable);
            StopLogging();
            return;
        }

        SetStatus(R.string.started);
    }

    /**
     * This method is called periodically to determine whether the cell tower /
     * gps providers have been enabled, and sets class level variables to those
     * values.
     */
    private void CheckTowerAndGpsStatus() {
        Session.setTowerEnabled(towerLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
        Session.setGpsEnabled(gpsLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
    }

    /**
     * Stops the location managers
     */
    private void StopGpsManager() {

        Utilities.LogDebug("Stopped GpsManager");

        if (towerLocationListener != null) {
            Utilities.LogDebug("Removing towerLocationManager updates");
            towerLocationManager.removeUpdates(towerLocationListener);
        }

        if (gpsLocationListener != null) {
            Utilities.LogDebug("Removing gpsLocationManager updates");
            gpsLocationManager.removeUpdates(gpsLocationListener);
            gpsLocationManager.removeGpsStatusListener(gpsLocationListener);
        }

        SetStatus(getString(R.string.stopped));
    }

    /**
     * Sets the current file name based on user preference.
     */
    private void ResetCurrentFileName(boolean newStart) {

        //Utilities.LogDebug("GpsLoggingService.ResetCurrentFileName");

        String newFileName = Session.getCurrentFileName();

        if (newStart) {
            //Get UserID for filename
            PreferencesHelper preferencesHelper = new PreferencesHelper(MyApplication.getContext());
            int userID = preferencesHelper.getUserId();
            Session.setCurrentFileName("" + userID + "_" + Session.getReminderUnixTime() + "_GPS");
            newFileName = Session.getCurrentFileName();
        }

        if (IsMainFormVisible()) {
            mainServiceClient.onFileName(newFileName);
        }

    }

    /**
     * Gives a status message to the main service client to display
     *
     * @param status The status message
     */
    void SetStatus(String status) {
        if (IsMainFormVisible()) {
            mainServiceClient.OnStatusMessage(status);
        }
    }

    /**
     * Gives an error message to the main service client to display
     *
     * @param messageId ID of string to lookup
     */
    void SetFatalMessage(int messageId) {
        if (IsMainFormVisible()) {
            mainServiceClient.OnFatalMessage(getString(messageId));
        }
    }

    /**
     * Gets string from given resource ID, passes to SetStatus(String)
     *
     * @param stringId ID of string to lookup
     */
    private void SetStatus(int stringId) {
        String s = getString(stringId);
        SetStatus(s);
    }

    /**
     * Notifies main form that logging has stopped
     */
    void StopMainActivity() {
        if (IsMainFormVisible()) {
            mainServiceClient.OnStopLogging();
        }
    }

    /**
     * Stops location manager, then starts it.
     */
    void RestartGpsManagers() {
        Utilities.LogDebug("Restarting GpsManagers");
        StopGpsManager();
        StartGpsManager();
    }


    /**
     * This event is raised when the GeneralLocationListener has a new location.
     * This method in turn updates notification, writes to file, reobtains
     * preferences, notifies main service client and resets location managers.
     *
     * @param loc Location object
     */
    void OnLocationChanged(Location loc) {
        int retryTimeout = Session.getRetryTimeout();

        if (!Session.isStarted()) {
            Utilities.LogDebug("OnLocationChanged called, but Session.isStarted is false");
            StopLogging();
            return;
        }

        Utilities.LogDebug("OnLocationChanged");


        long currentTimeStamp = System.currentTimeMillis();

        // Wait some time even on 0 frequency so that the UI doesn't lock up

        if ((currentTimeStamp - Session.getLatestTimeStamp()) < 1000) {
            return;
        }

        // Don't do anything until the user-defined time has elapsed
        if (!ForceLogOnce() && (currentTimeStamp - Session.getLatestTimeStamp()) < (AppSettings.getMinimumSeconds() * 1000)) {
            return;
        }

        // Don't do anything until the user-defined accuracy is reached
        if (AppSettings.getMinimumAccuracyInMeters() > 0) {
            if (AppSettings.getMinimumAccuracyInMeters() < Math.abs(loc.getAccuracy())) {
                if (retryTimeout < 50) {
                    Session.setRetryTimeout(retryTimeout + 1);
                    SetStatus("Only accuracy of " + String.valueOf(Math.floor(loc.getAccuracy())) + " reached");
                    StopManagerAndResetAlarm(AppSettings.getRetryInterval());
                    return;
                } else {
                    Session.setRetryTimeout(0);
                    SetStatus("Only accuracy of " + String.valueOf(Math.floor(loc.getAccuracy())) + " reached and timeout reached");
                    StopManagerAndResetAlarm();
                    return;
                }
            }
        }

        //Don't do anything until the user-defined distance has been traversed
        if (!ForceLogOnce() && AppSettings.getMinimumDistanceInMeters() > 0 && Session.hasValidLocation()) {

            double distanceTraveled = Utilities.CalculateDistance(loc.getLatitude(), loc.getLongitude(),
                    Session.getCurrentLatitude(), Session.getCurrentLongitude());

            if (AppSettings.getMinimumDistanceInMeters() > distanceTraveled) {
                SetStatus("Only " + String.valueOf(Math.floor(distanceTraveled)) + " m traveled.");
                StopManagerAndResetAlarm();
                return;
            }

        }

        Utilities.LogInfo("New location obtained");
        //New File Each Recording
        ResetCurrentFileName(false);
        Session.setLatestTimeStamp(System.currentTimeMillis());
        Session.setCurrentLocationInfo(loc);
        SetDistanceTraveled(loc);
        Notify();
        WriteToFile(loc);
        GetPreferences();
        StopManagerAndResetAlarm();
        SetForceLogOnce(false);

        if (IsMainFormVisible()) {
            mainServiceClient.OnLocationUpdate(loc);
        }
    }

    private void SetDistanceTraveled(Location loc) {
        // Distance
        if (Session.getPreviousLocationInfo() == null) {
            Session.setPreviousLocationInfo(loc);
        }
        // Calculate this location and the previous location location and add to the current running total distance.
        // NOTE: Should be used in conjunction with 'distance required before logging' for more realistic values.
        double distance = Utilities.CalculateDistance(
                Session.getPreviousLatitude(),
                Session.getPreviousLongitude(),
                loc.getLatitude(),
                loc.getLongitude());
        Session.setPreviousLocationInfo(loc);
        Session.setTotalTravelled(Session.getTotalTravelled() + distance);
    }

    protected void StopManagerAndResetAlarm() {
        //Utilities.LogDebug("GpsLoggingService.StopManagerAndResetAlarm");
        if (!AppSettings.shouldkeepFix()) {
            StopGpsManager();
        }
        SetAlarmForNextPoint();
    }

    protected void StopManagerAndResetAlarm(int retryInterval) {
        Utilities.LogDebug("StopManagerAndResetAlarm_retryInterval");
        if (!AppSettings.shouldkeepFix()) {
            StopGpsManager();
        }
        SetAlarmForNextPoint(retryInterval);
    }

    private void StopAlarm() {
        Utilities.LogDebug("Stopping Alarm");
        Intent i = new Intent(this, GpsLoggingService.class);
        i.putExtra("getnextpoint", true);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        nextPointAlarmManager.cancel(pi);
    }


    private void SetAlarmForNextPoint() {

        Utilities.LogDebug("SetAlarmForNextPoint");

        Intent i = new Intent(this, GpsLoggingService.class);

        i.putExtra("getnextpoint", true);

        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        nextPointAlarmManager.cancel(pi);

        nextPointAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + AppSettings.getMinimumSeconds() * 1000, pi);

    }

    private void SetAlarmForNextPoint(int retryInterval) {

        Utilities.LogDebug("SetAlarmForNextPoint_retryInterval");

        Intent i = new Intent(this, GpsLoggingService.class);

        i.putExtra("getnextpoint", true);

        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        nextPointAlarmManager.cancel(pi);

        nextPointAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + retryInterval * 1000, pi);

    }

    /**
     * Calls file helper to write a given location to a file.
     *
     * @param loc Location object
     */
    private void WriteToFile(Location loc) {
        Utilities.LogDebug("WriteToFile");
        List<IFileLogger> loggers = FileLoggerFactory.GetFileLoggers();
        Session.setAddNewTrackSegment(false);
        boolean atLeastOneAnnotationSuccess = false;

        for (IFileLogger logger : loggers) {
            try {
                logger.Write(loc);
                if (Session.hasDescription()) {
                    logger.Annotate(Session.getDescription(), loc);
                    atLeastOneAnnotationSuccess = true;
                }
            } catch (Exception e) {
                SetStatus(R.string.could_not_write_to_file);
            }
        }

        if (atLeastOneAnnotationSuccess) {
            Session.clearDescription();
            if (IsMainFormVisible()) {
                mainServiceClient.OnClearAnnotation();
            }
        }
    }

    /**
     * Informs the main service client of the number of visible satellites.
     *
     * @param count Number of Satellites
     */
    void SetSatelliteInfo(int count) {
        if (IsMainFormVisible()) {
            mainServiceClient.OnSatelliteCount(count);
        }
    }

    private boolean IsMainFormVisible() {
        return mainServiceClient != null;
    }


}
