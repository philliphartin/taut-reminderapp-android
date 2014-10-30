package ulster.serg.tautreminderapp.controller.helpers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import ulster.serg.tautreminderapp.model.csv.ReminderCSV;
import ulster.serg.tautreminderapp.model.sugarorm.Reminder;

/**
 * Created by philliphartin on 30/10/14.
 */
public class DatabaseImporter extends AsyncTask<String, String, Boolean> {
    private static final String LOG = "DatabaseImporter";

    private Context ApplicationContext;
    private Activity mActivity;
    private ProgressDialog dialog;
    private String doInBackgroundStatus;

    public DatabaseImporter(Activity activity) {
        super();
        mActivity = activity;
        ApplicationContext = mActivity.getApplicationContext();
        dialog = new ProgressDialog(mActivity);
    }

    private static CellProcessor[] getProcessors() {

        final CellProcessor[] processors = new CellProcessor[]{
                new NotNull(), //            format;
                new NotNull(), //            date;
                new NotNull(), //            time;
                new ParseInt(), //            unix;
                new NotNull(), //            dayofweek;
                new NotNull(), //            type;
                new NotNull(), //            description;
                new NotNull(), //            createdBy;
                new NotNull(), //            repeat;
                new ParseInt() //            createdById;
        };

        return processors;
    }

    // to show Loading dialog box
    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("Importing reminders...");
        this.dialog.show();
    }

    // to write process
    protected Boolean doInBackground(final String... args) {

        //Build Filepath
        StringBuilder filepath = new StringBuilder();
        filepath.append("/sdcard/tautreminder_export");
        filepath.append(".csv");
        String CSV_FILENAME = filepath.toString();

        ICsvBeanReader beanReader = null;
        try {
            beanReader = new CsvBeanReader(new FileReader(CSV_FILENAME), CsvPreference.STANDARD_PREFERENCE);

            final String[] header = beanReader.getHeader(true);
            final CellProcessor[] processors = getProcessors();

            ReminderCSV reminderCSV;
            while ((reminderCSV = beanReader.read(ReminderCSV.class, header, processors)) != null) {

                if (reminderCSV.getFormat().equals("basic")) {
                    ReminderHelper reminderHelper = new ReminderHelper(ApplicationContext);
                    reminderHelper.saveNewReminder(new Reminder(reminderCSV));
                } else {
                    Log.i(LOG, "Reminder is voice, will not import");
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            doInBackgroundStatus = "No CSV found";
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            doInBackgroundStatus = e.getMessage();
            return false;
        } finally {
            if (beanReader != null) {
                try {
                    beanReader.close();

                } catch (IOException e) {
                    e.printStackTrace();
                    doInBackgroundStatus = e.getMessage();

                    return false;
                }
            }
        }

        doInBackgroundStatus = "Import success";
        return true;
    }

    // close dialog and give msg
    protected void onPostExecute(Boolean success) {
        if (this.dialog.isShowing()) {
            this.dialog.dismiss();
        }

        Toast.makeText(ApplicationContext, doInBackgroundStatus, Toast.LENGTH_LONG).show();
    }

}
