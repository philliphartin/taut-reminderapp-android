package ulster.serg.tautreminderapp.controller.demo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import ulster.serg.tautreminderapp.controller.helpers.DateHelper;
import ulster.serg.tautreminderapp.controller.helpers.ReminderHelper;
import ulster.serg.tautreminderapp.model.sugarorm.Reminder;

import java.util.Random;

/**
 * Created by philliphartin on 23/09/2014.
 */
public class DemoReminders {
    private static final String LOG = "DemoReminders";

    Context mContext;

    public DemoReminders(Context context) {
        this.mContext = context;
    }

    public void generateRemindersForDemos() {

        ReminderHelper reminderHelper = new ReminderHelper(mContext);

        DateHelper dateHelper = new DateHelper();
        long numberOfReminders = 7;
        int minuteSpacer = 3; //Change this value to set the time between reminders

        for (int i = 0; i < numberOfReminders; i++) {

            int minutesToAdd = (i + 1) * minuteSpacer;

            long unixTime = dateHelper.getUnixTimePlusMinutes(minutesToAdd);
            String date = dateHelper.getDateSaveReadableFromUnixTime(unixTime);
            String time = dateHelper.getTimeSaveReadableFromUnixTime(unixTime);
            String dayOfWeek = dateHelper.getDayOfTheWeekFromUnixTime(unixTime);

            Reminder reminder = new Reminder();
            reminder.setFormat("basic");
            reminder.setUnixtime(unixTime);
            reminder.setDate(date);
            reminder.setTime(time);
            reminder.setDayofweek(dayOfWeek);
            reminder.setType(returnReminderTypeForNumber(i));
            reminder.setDescription(getDescriptionForReminder(reminder.getType()));
            reminder.setRepeatfreq(getRandomRepeatFreq());
            reminder.setCreatedby("User");
            reminder.setCreatedbyid(9999);
            reminder.setActive(1);

            reminderHelper.saveNewReminder(reminder);
        }

        Toast.makeText(mContext, "Generated " + numberOfReminders + " reminders", Toast.LENGTH_SHORT).show();
        Log.d(LOG, "Generated " + numberOfReminders + " reminders");

    }

    private String returnReminderTypeForNumber(int number) {

        String type;
        switch (number) {
            case 0:
                type = "Medication";
                break;
            case 1:
                type = "Appointment";
                break;
            case 2:
                type = "Meal";
                break;
            case 3:
                type = "Drink";
                break;
            case 4:
                type = "Personal Hygiene";
                break;
            case 5:
                type = "Charge Phone";
                break;
            case 6:
                type = "Other";
                break;
            default:
                type = "Meal";
                break;
        }
        return type;
    }

    private String getDescriptionForReminder(String reminderType) {

        String description;

        if (reminderType.equals("Medication")) {
            description = "Take 2 of your heart tablets";

        } else if (reminderType.equals("Appointment")) {
            description = "You have a doctors appointment in 1 hour.";

        } else if (reminderType.equals("Meal")) {
            description = "It's dinner time!";

        } else if (reminderType.equals("Drink")) {
            description = "Have a wee cup of tea";

        } else if (reminderType.equals("Charge Phone")) {
            description = "Don't forget to charge this phone";

        } else if (reminderType.equals("Other")) {
            description = "Bert said he would call over today";

        } else if (reminderType.equals("Personal Hygiene")) {
            description = "Don't forget to brush your teeth";
        } else {
            throw new IllegalArgumentException("Invalid reminderType: " + reminderType);
        }
        return description;
    }

    private String getRandomRepeatFreq() {
        Random rnd = new Random();
        int random = rnd.nextInt(3);
        String type;

        switch (random) {
            case 0:
                type = "Never";
                break;
            case 1:
                type = "Everyday";
                break;
            case 2:
                type = "Weekly";
                break;
            default:
                type = "Never";
                break;
        }
        return type;
    }


}
