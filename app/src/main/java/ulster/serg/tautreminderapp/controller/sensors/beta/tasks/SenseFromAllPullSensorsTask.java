package ulster.serg.tautreminderapp.controller.sensors.beta.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.ubhave.dataformatter.DataFormatter;
import com.ubhave.dataformatter.json.JSONFormatter;
import com.ubhave.sensormanager.ESException;
import com.ubhave.sensormanager.ESSensorManager;
import com.ubhave.sensormanager.config.GlobalConfig;
import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.sensors.SensorEnum;
import com.ubhave.sensormanager.sensors.SensorUtils;

import ulster.serg.tautreminderapp.controller.MyApplication;
import ulster.serg.tautreminderapp.controller.helpers.FileHelper;
import ulster.serg.tautreminderapp.controller.helpers.PreferencesHelper;

public class SenseFromAllPullSensorsTask extends AsyncTask<Void, Void, Void> {
    private final static String LOG_TAG = "SenseFromAllPullSensorsTask";
    private ESSensorManager sensorManager;
    private String reminderID;
    private String reminderDate;
    private String reminderTime;

    public SenseFromAllPullSensorsTask(final Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        reminderID = extras.getString("reminderIdentifier");
//        long recordingStartTime = extras.getLong("recordingStartTime");
//        long recordingDurationInMs = extras.getLong("recordingDurationInMillis");
        this.reminderDate = extras.getString("reminderDate");
        this.reminderTime = extras.getString("reminderTime");

        try {
            sensorManager = ESSensorManager.getSensorManager(context);
            sensorManager.setGlobalConfig(GlobalConfig.PRINT_LOG_D_MESSAGES, false);
        } catch (ESException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d("Sensor Data", " === Starting " + LOG_TAG + " ===");
        for (SensorEnum s : SensorEnum.values()) {
            if (s.isPull()) {
                try {
                    // Sense with default parameters
                    Log.d(LOG_TAG, "Sensing from: " + s.getName());
                    SensorData data = sensorManager.getDataFromSensor(s.getType());
                    Log.d(LOG_TAG, "Sensed from: " + SensorUtils.getSensorName(data.getSensorType()));

                    // To store/format your data, check out the SensorDataManager library
                    JSONFormatter jsonFormatter = DataFormatter.getJSONFormatter(MyApplication.getContext(), s.getType());
                    String jsonToWrite = (jsonFormatter.toJSON(data)).toString();
                    writeToFile(s, jsonToWrite);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("Sensor Data", " === Finished " + LOG_TAG + " ===");
        return null;
    }

    private void writeToFile(SensorEnum sensorEnum, String jsonToWrite) {
        String sensor = sensorEnum.getName();

        PreferencesHelper preferencesHelper = new PreferencesHelper(MyApplication.getContext());
        StringBuilder stringBuilder = new StringBuilder();
        String UNDERSCORE = "_";
        stringBuilder.append(preferencesHelper.getUserIdAsString());
        stringBuilder.append(UNDERSCORE);
        stringBuilder.append(reminderDate);
        stringBuilder.append(UNDERSCORE);
        stringBuilder.append(reminderTime);
        stringBuilder.append(UNDERSCORE);
        stringBuilder.append(sensor);

        FileHelper fileHelper = new FileHelper(MyApplication.getContext());
        String filename = stringBuilder.toString();
        fileHelper.write(filename, jsonToWrite);

    }

}
