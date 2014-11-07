package ulster.serg.tautreminderapp.controller.helpers;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import ulster.serg.tautreminderapp.model.sugarorm.Reminder;

/**
 * Created by philliphartin on 26/09/2014.
 */
public class FileHelper {

    private final Context context;
    private Writer writer;
    private String absolutePath;

    public FileHelper(Context context) {
        super();
        this.context = context;
    }

    public void write(String fileName, String data) {
        File root = Environment.getExternalStorageDirectory();
        File outDir = new File(root.getAbsolutePath() + File.separator + "taut_logger");
        if (!outDir.isDirectory()) {
            outDir.mkdir();
        }
        try {
            if (!outDir.isDirectory()) {
                throw new IOException(
                        "Unable to create directory 'taut_logger' Maybe the SD card is mounted?");
            }
            File outputFile = new File(outDir, fileName);
            writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(data);

            Log.d("FileHelper", "Report successfully saved to: " + outputFile.getAbsolutePath());
//            Toast.makeText(context.getApplicationContext(),
//                    "Report successfully saved to: " + outputFile.getAbsolutePath(),
//                    Toast.LENGTH_LONG).show();
            writer.close();
        } catch (IOException e) {
            Log.w("FileHelper", e.getMessage(), e);
//            Toast.makeText(context, e.getMessage() + " Unable to write to external storage.",
//                    Toast.LENGTH_LONG).show();
        }

    }

    public Writer getWriter() {
        return writer;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void deleteAudioFileForReminder(Reminder reminder) {
        String filepath = reminder.getAudiofilepath();
        File file = new File(filepath);
        file.delete();
    }
}