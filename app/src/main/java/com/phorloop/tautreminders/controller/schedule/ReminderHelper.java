package com.phorloop.tautreminders.controller.schedule;

        import com.orm.query.Select;
        import com.phorloop.tautreminders.model.sugarorm.Reminder;

        import java.util.List;

/**
 * Created by philliphartin on 22/09/2014.
 */

public class ReminderHelper {

    public ReminderHelper() {
    }

    public Reminder getNextReminder(){
       // List<Reminder> reminders = Reminder.find(Reminder.class, "active = ? orderBy = ?", "1", "unixtime");
        List<Reminder> reminders = Select.from(Reminder.class).where("active = 1").orderBy("unixtime").limit("1").list();

        return  reminders.get(0);
    }
}
