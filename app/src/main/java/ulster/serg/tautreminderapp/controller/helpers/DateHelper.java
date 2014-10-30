package ulster.serg.tautreminderapp.controller.helpers;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by philliphartin on 25/09/2014.
 */
public class DateHelper {
    private static final String LOG = "DateHelper";

    //For DB
    public String getDateSaveReadableFromDateTime(DateTime dateTime) {

        //Create Formats for display
        String format = "MM-dd-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, dateTime.getYear());
        calendar.set(Calendar.MONTH, dateTime.getMonthOfYear());
        calendar.set(Calendar.DAY_OF_MONTH, dateTime.getDayOfMonth());

        return sdf.format(calendar.getTime());
    }

    public String getTimeSaveReadableFromDateTime(DateTime dateTime) {

        //Create Formats for display
        String format = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, dateTime.getHourOfDay());
        calendar.set(Calendar.MINUTE, dateTime.getMinuteOfHour());

        return sdf.format(calendar.getTime());
    }

    public String getDateSaveReadableFromUnixTime(long unixTime) {

        Instant instant = new Instant(unixTime);
        DateTime dateTime = instant.toDateTime();

        return getDateSaveReadableFromDateTime(dateTime);
    }

    public String getTimeSaveReadableFromUnixTime(long unixTime) {

        Instant instant = new Instant(unixTime);
        DateTime dateTime = instant.toDateTime();

        return getTimeSaveReadableFromDateTime(dateTime);
    }

    //Human Readable
    public String getDateHumanReadableFromUnixTime(long unixTime) {

        Instant instant = new Instant(unixTime);
        DateTime dateTime = instant.toDateTime();

        //Create Formats for display
        String format = "MMMM dd, yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, dateTime.getYear());
        calendar.set(Calendar.MONTH, dateTime.getMonthOfYear() - 1); // Minus 1 to get correct index of month for calendar object
        calendar.set(Calendar.DAY_OF_MONTH, dateTime.getDayOfMonth());

        return sdf.format(calendar.getTime());
    }

    public String getTimeHumanReadableFromUnixTime(long unixTime) {

        Instant instant = new Instant(unixTime);
        DateTime dateTime = instant.toDateTime();

        //Create Formats for display
        String format = "h:mm aa"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, dateTime.getHourOfDay());
        calendar.set(Calendar.MINUTE, dateTime.getMinuteOfHour());

        return sdf.format(calendar.getTime());
    }

    public String getDateHumanReadableFromCalendar(Calendar calendar) {

        //Create Formats for display
        String format = "MMMM dd, yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        return sdf.format(calendar.getTime());
    }

    public String getTimeHumanReadableFromCalendar(Calendar calendar) {

        //Create Formats for display
        String format = "h:mm aa"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

        return sdf.format(calendar.getTime());
    }

    public String getDayOfTheWeekFromUnixTime(long unixtime) {
        Instant instant = new Instant(unixtime);
        DateTime dateTime = instant.toDateTime();

        return dateTime.dayOfWeek().getAsText();
    }

    public long getUnixTimePlusMinutes(int minutes) {

        Instant instant = new Instant(getUnixTimeNow());
        DateTime dateTime = instant.toDateTime();
        dateTime = dateTime.plusMinutes(minutes);

        return dateTime.getMillis();

    }

    public long getUnixTimeNow() {
        Instant instant = Instant.now();
        return instant.getMillis();
    }

}
