package ulster.serg.tautreminderapp.controller.sensors.legacy.sensor.recordings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by philliphartin on 09/03/2014.
 */
public class ZipRecordings extends AsyncTask<String, Void, Boolean> {
    private static final String LOG = "ZipRecordings";
    Context contextGUI;
    SharedPreferences sharedPreferences;
    File zipFile;

    public ZipRecordings(Context callerclass) {
        contextGUI = callerclass;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        try {
            //Set Folders
            File logsFolder = new File(Environment.getExternalStorageDirectory(), "TAUT/SensorLogs");
            File logsZipFolder = new File(Environment.getExternalStorageDirectory(), "TAUT/SensorLogs/Zipped");
            if(!logsZipFolder.exists())
            {
                logsZipFolder.mkdirs();
            }

            //Create List of Files in log folder. Including only CSV & GPX FILES
            List<File> files = new ArrayList<File>(Arrays.asList(logsFolder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String s) {
                    //return !s.contains("zip") || !s.contains("log") || file.isFile();
                    return s.contains("csv") || s.contains("gpx");
                }
            })));

            //If there are files to zip then create zip file in directory
            if (files.size()>0){
                Log.d(LOG, files.size() + " files to compress");
                ArrayList<String> filePaths = createZipFile(files, logsZipFolder);
                ZipHelper zh = new ZipHelper(filePaths.toArray(new String[filePaths.size()]), zipFile.getAbsolutePath());
                zh.Zip();
                //Once zipped delete the files
                deleteFiles(files);
                files.clear();
            }else {
                Log.d(LOG, "No files to zip");
            }
            return true;

        }catch (Exception e){
            Log.e(LOG, e.toString());
            return false;
        }
    }

    private void deleteFiles(List<File> files){
        try{
            for (File f : files) {
                String filePath = f.getAbsolutePath();
                f.delete();
                Log.d(LOG, "Deleted: " + filePath);
            }
        }catch (Exception e){
            Log.e(LOG, e.toString());
        }
    }

    private ArrayList<String> createZipFile(List<File> files, File zipFolder){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(contextGUI);
        String userID = sharedPreferences.getString("user_id", "");
        Log.d(LOG, "UserID " + userID);
        DateTime dateTime = new DateTime();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("MM_dd_yyyy_HH_mm");
        String dateFormatted = fmt.print(dateTime);
        zipFile = new File(zipFolder.getPath(), userID + "_" + dateFormatted + ".zip");
        Log.d(LOG, "Zip FileName " + zipFile.getName());

        ArrayList<String> filePaths = new ArrayList<String>();
        for (File f : files) {
            filePaths.add(f.getAbsolutePath());
        }
        return filePaths;
    }
}


