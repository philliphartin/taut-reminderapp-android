//package com.phorloop.tautreminders.controller.schedule;
//
//import android.content.Context;
//import android.util.Log;
//
//import com.phorloop.tautreminders.model.sugarorm.Reminder;
//
//import org.joda.time.DateTime;
//import org.joda.time.Instant;
//
//import java.util.Calendar;
//
///**
// * Created by philliphartin on 22/09/2014.
// */
//public class ScheduleHelper {
//
//    private static final String LOG = "ScheduleHelper";
//    long reminderID;
//    Context refContext = null;
//    //JodaTime objects
//    DateTime dateTimeReminder, dateTimeCurrent;
//
//    public ScheduleHelper(Context context) {
//        refContext = context;
//    }
//
//    public int[] checkDateMatchSimple() {
//
//        int[] returnArray = new int[2];
//        dateTimeCurrent = getDateDetails_NOW();
//
//        //Open DatabasHelper
//        ReminderHelper reminderHelper = new ReminderHelper();
//        //DatabaseHelper db = new DatabaseHelper(refContext);
//
//        try {
//            //Get NextReminder User
//            Reminder reminderNext = reminderHelper.getNextReminder();
//
//            reminderID = reminderNext.getId();
//            dateTimeReminder = getDateDetails_REMINDER(reminderID);
//
//            //CHECK IF REMINDER DATE IS NOW
//            Boolean dateEqual;
//
//            if (dateTimeReminder.getYear() == dateTimeCurrent.getYear()) {
//                if (dateTimeReminder.getMonthOfYear() == dateTimeCurrent.getMonthOfYear()) {
//                    if (dateTimeReminder.getDayOfMonth() == dateTimeCurrent.getDayOfMonth()) {
//                        if (dateTimeReminder.getHourOfDay() == dateTimeCurrent.getHourOfDay()) {
//                            if (dateTimeReminder.getMinuteOfDay() == dateTimeCurrent.getMinuteOfDay()) {
//                                //SET RETURN VALUES TO TRUE
//
//                                Log.d(LOG, "ReminderTime: " + (dateTimeReminder.getMillis() / 1000L));
//                                returnArray[0] = 1;
//                                returnArray[1] = reminderID;
//                                dateEqual = true;
//                            } else {
//                                dateEqual = false;
//                            }
//                        } else {
//                            dateEqual = false;
//                        }
//                    } else {
//                        dateEqual = false;
//                    }
//                } else {
//                    dateEqual = false;
//                }
//            } else {
//                dateEqual = false;
//            }
//
//            if (!dateEqual) {
//                //SET RETURN VALUES TO FALSE / NO POPUP
//                returnArray[0] = 0;
//                returnArray[1] = reminderID;
//
//                //The date is not now. So check date to determine if in future or past
//                compareDates(reminderID, dateTimeCurrent, dateTimeReminder);
//            }
//
//        } catch (Exception e) {
////            db.closeDB();
//            Log.e(LOG, "Error: NO REMINDERS");
//        }
//
//        return returnArray;
//    }
//
//    public void compareDates(int reminderID, DateTime current, DateTime reminder) {
//
//        //Convert Back to unixTime ISO standard (divide by 1000)
//        long unixtimeReminder = reminder.getMillis() / 1000L;
//        long unixtimeCurrent = current.getMillis() / 1000L;
//        long unixtimeDiff = (unixtimeReminder - unixtimeCurrent);
//
//        DatabaseHelper db = new DatabaseHelper(refContext);
//        int duration;
//
//        try {
//            duration = db.getVoiceReminder(reminderID).getDuration();
//        } catch (Exception e) {
//            duration = 0;
//            Log.d(LOG, "No duration set");
//        }
//
//        Log.v(LOG, "Time Current: " + unixtimeCurrent + " | Time Reminder: " + unixtimeReminder);
//        Log.v(LOG, "Time Difference (ms): " + unixtimeDiff);
//
//        if (unixtimeDiff < 0) {
//            Log.w(LOG, "Next Reminder in the past");
//            reScheduleReminder(reminderID, 2, 0, 0, duration, 0);
//        } else {
//            Log.i(LOG, "Next Reminder in the future");
//            //Do Nothing
//        }
//
//        db.closeDB();
//    }
//
//    public void reScheduleReminder(int reminderID, int acknowledged, int timeElapsed, int batterylevel, int voiceduration, int listencount) {
//
//        //Acknowledged 0=No 1=Yes 2=Missed
//        DatabaseHelper db = new DatabaseHelper(refContext);
//        //Get DateTime object of reminder
//        DateTime reminderDT_old = getDateDetails_REMINDER(reminderID);
//
//        //Get RepeatType of reminder
//        String repeat = db.getSingleReminder(reminderID).getRepeat().toString();
//
//        if (repeat.contains("Never")) {
//            Log.d(LOG, "Repeat Type: " + repeat);
//            db.deleteReminder(reminderID);
//
//            //Create Log Entry
//            createLogEntry(reminderID, acknowledged, timeElapsed, batterylevel, voiceduration, listencount);
//            db.closeDB();
//
//            //CHECK DATES AGAIN
//            checkDateMatchSimple();
//        }
//
//        if (repeat.contains("Everyday")) {
//            Log.d(LOG, "Repeat Type: " + repeat);
//
//            //Create Log Entry
//            createLogEntry(reminderID, acknowledged, timeElapsed, batterylevel, voiceduration, listencount);
//
//            //ADD 1 DAY to DATETIME
//            DateTime reminderDT_new = reminderDT_old.plusDays(1);
//
//            Log.d(LOG,
//                    "resch/NEW Y:" + reminderDT_new.getYear()
//                            + " M:" + reminderDT_new.getMonthOfYear()
//                            + " D:" + reminderDT_new.getDayOfMonth()
//                            + " H:" + reminderDT_new.getHourOfDay()
//                            + " M:" + reminderDT_new.getMinuteOfHour());
//
//            //FORMAT DATE TO STRINGS FOR DB
//            long newTime = reminderDT_new.getMillis();
//            long newTimeforDB = (newTime / 1000L);
//
//            DateTimeFormatter fmt_date = DateTimeFormat.forPattern("MM-dd-yyyy");
//            String new_date = fmt_date.print(newTime);
//            Log.w(LOG, "New Date for DB: " + new_date);
//
//            //Run Reschedule Method
//            db.updateRepeatDate(reminderID, new_date, newTimeforDB);
//            db.closeDB();
//
//            //CHECK DATES AGAIN
//            checkDateMatchSimple();
//        }
//
//        if (repeat.contains("Weekly")) {
//            //Add 1 Week to Current Date
//            Log.d(LOG, "Repeat Type: " + repeat);
//
//            //Create Log Entry
//            createLogEntry(reminderID, acknowledged, timeElapsed, batterylevel, voiceduration, listencount);
//
//            //ADD 1 DAY to DATETIME
//            DateTime reminderDT_new = reminderDT_old.plusWeeks(1);
//            Log.i(LOG,
//                    "resch/NEW Y:" + reminderDT_new.getYear()
//                            + " M:" + reminderDT_new.getMonthOfYear()
//                            + " D:" + reminderDT_new.getDayOfMonth()
//                            + " H:" + reminderDT_new.getHourOfDay()
//                            + " M:" + reminderDT_new.getMinuteOfHour());
//
//            //FORMAT DATE TO STRINGS FOR DB
//            long newTime = reminderDT_new.getMillis();
//            long newTimeforDB = (newTime / 1000L);
//            DateTimeFormatter fmt_date = DateTimeFormat.forPattern("MM-dd-yyyy");
//            String new_date = fmt_date.print(newTime);
//            Log.w(LOG, "New Date for DB: " + new_date);
//            //Run Reschedule Method
//            db.updateRepeatDate(reminderID, new_date, newTimeforDB);
//            db.closeDB();
//            //CHECK DATES AGAIN
//            checkDateMatchSimple();
//        }
//        db.closeDB();
//    }
//
//    //GET DateTime Objects
//    public DateTime getDateDetails_REMINDER(long reminderID) {
//        DatabaseHelper db0 = new DatabaseHelper(refContext);
//
//        long reminderTime = (db0.getSingleReminder(reminderID).getUnix()) * 1000L;
//        Instant instant = new Instant(reminderTime);
//        DateTime dateTime = instant.toDateTime();
//        Log.w(LOG,
//                "check/REM Y:" + dateTime.getYear()
//                        + " M:" + dateTime.getMonthOfYear()
//                        + " D:" + dateTime.getDayOfMonth()
//                        + " H:" + dateTime.getHourOfDay()
//                        + " M:" + dateTime.getMinuteOfHour());
//
//        //db0.closeDB();
//        return dateTime;
//    }
//
//    public DateTime getDateDetails_NOW() {
//        Calendar calendar = Calendar.getInstance();
//        Instant instant = new Instant(calendar.getTimeInMillis());
//        DateTime dateTime = instant.toDateTime();
//        Log.i(LOG,
//                "check/NOW Y:" + dateTime.getYear()
//                        + " M:" + dateTime.getMonthOfYear()
//                        + " D:" + dateTime.getDayOfMonth()
//                        + " H:" + dateTime.getHourOfDay()
//                        + " M:" + dateTime.getMinuteOfHour());
//        return dateTime;
//    }
//
//    public void createLogEntry(int reminderID, int acknowledged, int timeElapsed, int batterylevel, int voiceduration, int listencount) {
//
//        DatabaseHelper db = new DatabaseHelper(refContext);
//        db.getSingleReminder(reminderID);
//
//        //GET Reminder User from DB
//        String format = db.getSingleReminder(reminderID).getFormat();
//        String date = db.getSingleReminder(reminderID).getDate();
//        String time = db.getSingleReminder(reminderID).getTime();
//        int unixtime = db.getSingleReminder(reminderID).getUnix();
//        String dayofweek = db.getSingleReminder(reminderID).getDayofweek();
//        String type = db.getSingleReminder(reminderID).getType();
//        //String desc = db.getSingleReminder(reminderID).getDescription(); IF DESC IS WANTED CAN BE ADDED LATER
//        String repeat = db.getSingleReminder(reminderID).getRepeat();
//        String createdby = db.getSingleReminder(reminderID).getCreatedBy();
//        int createdbyid = db.getSingleReminder(reminderID).getCreatedById();
//
//        //Get PatientID
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(refContext);
//        String patientid_pref = sharedPreferences.getString("user_id", "");
//        int patientid = Integer.parseInt(patientid_pref);
//
//        Logs logs = new Logs(1, patientid, format, time, date, unixtime, dayofweek, type, repeat, createdby, createdbyid, acknowledged, timeElapsed, batterylevel, voiceduration, listencount);
//        // Insert Log Entry
//        db.createLogEntry(logs);
//        db.closeDB();
//    }
//}
