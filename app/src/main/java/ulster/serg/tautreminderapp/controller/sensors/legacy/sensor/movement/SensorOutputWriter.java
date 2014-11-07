package ulster.serg.tautreminderapp.controller.sensors.legacy.sensor.movement;

import android.os.Environment;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ulster.serg.tautreminderapp.controller.MyApplication;
import ulster.serg.tautreminderapp.controller.helpers.PreferencesHelper;
import ulster.serg.tautreminderapp.controller.sensors.legacy.sensor.location.common.Session;

public class SensorOutputWriter {
    public static final String TYPE_ACCELEROMETER = "Accelerometer";
    public static final String TYPE_ORIENTATION = "Orientation";
    public static final String TYPE_MAGNETIC = "Magnetic";
    public static final String TYPE_LIGHT = "Light";
    public static final String TYPE_PROXIMITY = "Proximity";
    public static final String TYPE_LINEARACCELEROMETER = "LinearAccelerometer";
    public static final String TYPE_GYROSCOPE = "Gyroscope";
    public static final String TYPE_AMBIENTTEMP = "AmbientTemperature";
    public static final String TYPE_INTENT = "_Debug";
    public static final String TYPE_OTHER_UNKNOWN = "Other-unknown";
    public static final String OUTPUT_DIRECTORY = "taut_logger/LegacyLogs";
    public static final int BUFFER_SIZE = 100;
    private static final String LOG = "SensorOutputWriter";
    private File outputDirectory;
    private BufferedOutputStream output;
    private String sensorType;
    private long startTime;
    private CircularFloatArrayBuffer mBuffer;
    private int userID;


    public SensorOutputWriter(String sensorType) throws StorageErrorException {
        this.sensorType = sensorType;

        PreferencesHelper preferencesHelper = new PreferencesHelper(MyApplication.getContext());

        userID = preferencesHelper.getUserId();
        ensureOutputDirectoryExists();

        mBuffer = new CircularFloatArrayBuffer(BUFFER_SIZE);

        try {
            startLog();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new StorageErrorException("Could not open file for writing:" + e.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new StorageErrorException("Could not open file for writing:" + e.getLocalizedMessage());
        }
    }

    public void writeReadings(float readings[]) throws StorageErrorException {
        DateTime dateTime = new DateTime();
        long currentTime = dateTime.getMillis();
        long timeOffset = System.currentTimeMillis() - startTime;
        StringBuilder sb = new StringBuilder();

        // Construct string to write in output and array to store in buffer
        sb.append(currentTime);
        float toBuffer[] = new float[readings.length + 1];
        toBuffer[0] = timeOffset;
        for (int i = 0; i < readings.length; i++) {
            sb.append("," + readings[i]);
            toBuffer[i + 1] = readings[i];
        }

        // Write string in output and add to buffer
        writeLine(sb.toString());
        mBuffer.add(toBuffer);

    }

    public void writeLines(String lines[]) throws StorageErrorException {
        for (String line : lines) {
            writeLine(line);
        }
    }

    public void writeLine(String line) throws StorageErrorException {
        String str = line + "\n";
        try {
            output.write(str.getBytes());
        } catch (IOException e) {
            throw new StorageErrorException("Could not write to output: " + e.getLocalizedMessage());
        }
    }

    public void close() throws StorageErrorException {
        endLog();
    }

    private void ensureOutputDirectoryExists() throws StorageErrorException {
        // See if external storage is present
        String storageStatus = Environment.getExternalStorageState();
        if (!storageStatus.equals(Environment.MEDIA_MOUNTED)) {
            // If not present, launch exception
            throw new StorageErrorException("External storage state is: " + storageStatus);
        }

        // Ensure output dir exists
        File dir = Environment.getExternalStorageDirectory();
        outputDirectory = new File(dir, OUTPUT_DIRECTORY);
        if (outputDirectory.exists())
            return;
        if (!outputDirectory.mkdir()) {
            throw new StorageErrorException("Could not create: " + outputDirectory.getAbsolutePath());
        }
    }

    private void startLog() throws StorageErrorException, IOException {
        // Get current time
        DateTime now = new DateTime();
        startTime = now.getMillis();

        // Compute file name
        //TODO: Get the unixtime for reminder
        String fileName = "" + userID + "_" + Session.getReminderUnixTime() + "_" + sensorType + ".csv";
        // Create file
        File out = new File(outputDirectory, fileName);
        out.createNewFile();
        Log.w(LOG, "Writing to: " + out.getAbsolutePath());
        output = new BufferedOutputStream(new FileOutputStream(out));

        // Insert initial data
//        String lines[] = {"# === BEGINNING OUTPUT DUMP at " + formatDate(now, false) + " === #"};
//        writeLines(lines);
    }

    private void endLog() throws StorageErrorException {
//        DateTime now = new DateTime();
//        endTime = now.getMillis();
//        recordingDuration = (endTime - startTime);
//        String endtime = "# === ENDING OUTPUT DUMP at " + formatDate(now, false) + " === #";
//        String duration = "# === RECORDING DURATION (ms): " + recordingDuration + " === #";
//        writeLine(endtime);
//        writeLine(duration);
        try {
            output.flush();
            output.close();
        } catch (IOException e) {
            throw new StorageErrorException("Could not close the output: " + e.getLocalizedMessage());
        }
    }

    private String formatDate(DateTime date, boolean forFilePath) {
        String format = forFilePath ? "yyyy-MM-dd_HH.mm" : "yyyy/MM/dd HH:mm";
        DateTimeFormatter fmt_date = DateTimeFormat.forPattern(format);
        String formattedDate = fmt_date.print(date);
        return formattedDate;
    }

    public float[][] getBuffer() {
        if (mBuffer.getSize() == 0) {
            return new float[0][0];
        } else {
            float[][] result = (float[][]) mBuffer.getContents();
            return result;
        }
    }

}
