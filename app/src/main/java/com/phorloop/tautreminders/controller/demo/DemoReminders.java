package com.phorloop.tautreminders.controller.demo;

import com.phorloop.tautreminders.controller.helpers.DateHelper;
import com.phorloop.tautreminders.model.sugarorm.Reminder;

import java.util.Random;

/**
 * Created by philliphartin on 23/09/2014.
 */
public class DemoReminders {

    public void generateRemindersForDemos() {
        DateHelper dateHelper = new DateHelper();
        long numberOfReminders = 7;
        for (int i = 0; i < numberOfReminders; i++) {

            int minutesToAdd = (i + 1) * 5;

            long unixTime = dateHelper.getUnixTimePlusMinutes(minutesToAdd);
            String date = dateHelper.getDateSaveReadableFromUnixTime(unixTime);
            String time = dateHelper.getTimeSaveReadableFromUnixTime(unixTime);
            String dayOfWeek = dateHelper.getDayOfTheWeekFromUnixTime(unixTime);

            Reminder reminder = new Reminder();
            reminder.setUnixtime(unixTime);
            reminder.setDate(date);
            reminder.setTime(time);
            reminder.setDayofweek(dayOfWeek);
            reminder.setType(returnReminderTypeForNumber(i));
            reminder.setDescription(getDescriptionForReminder(reminder.getType())); // TODO: Finish this return statement
            reminder.setRepeatfreq(getRandomRepeatFreq());
            reminder.setCreatedby("User");
            reminder.setCreatedbyid(1234);
            reminder.setActive(1);
        }
    }

    private String returnReminderTypeForNumber(int number){

        String type;
        switch (number) {
            case 0: type = "Medication";
                break;
            case 1:  type = "Appointment";
                break;
            case 2:  type = "Meal";
                break;
            case 3:  type = "Drink";
                break;
            case 4:  type = "Personal Hygiene";
                break;
            case 5:  type = "Charge Phone";
                break;
            case 6:  type = "Other";
                break;
            default: type = "Meal";
                break;
        }
        return type;
    }

    private String getDescriptionForReminder(String reminderType){
        return "Template description";
    }

    private String getRandomRepeatFreq(){
        Random rnd = new Random();
        int random = rnd.nextInt(2);
        String type;

        switch (random) {
            case 0: type = "Never";
                break;
            case 1:  type = "Daily";
                break;
            case 2:  type = "Weekly";
                break;
            default: type = "Never";
                break;
        }
        return type;
    }


}
