package com.phorloop.tautreminders.view.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.phorloop.tautreminders.R;
import com.phorloop.tautreminders.controller.customListViewAdapter.cListAdapter;
import com.phorloop.tautreminders.controller.customListViewAdapter.cListItem;
import com.phorloop.tautreminders.controller.helpers.ReminderHelper;
import com.phorloop.tautreminders.model.sugarorm.Reminder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Phillip J Hartin on 21/10/13.
 */
public class ViewRemindersListActivity extends Activity {
    private static final String LOGa = "ViewRemindersListActivity";
    private Context context = this;


    public int selectedItem = -1;
    public ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.pressmenu_listitem, menu);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menuitem_delete:
                    deleteItem(selectedItem);
                    mode.finish();
                    return true;
                case R.id.menuitem_desc:
                    showDesc(selectedItem);
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        // called when the user exits the action mode
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

    protected Object mActionMode;
    ListView listView;
    cListAdapter listAdapter;
    ArrayList activeRemindersList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_list);

        //Setup Actionbar
        ActionBar actionBar = getActionBar();
        actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle("View All Reminders");


        //List Setup
        activeRemindersList = getListData();
        listView = (ListView) findViewById(R.id.custom_list);
        listAdapter = new cListAdapter(this, activeRemindersList);
        listView.setAdapter(listAdapter);

        //ListItem short click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                selectedItem = position;
                mActionMode = ViewRemindersListActivity.this
                        .startActionMode(mActionModeCallback);
                v.setSelected(true);
            }
        });
    }

    private ArrayList getListData() {
        Calendar calendar = Calendar.getInstance();
        //GET CURRENT DATE AS UNIX TIME
        long unixCurrentTime = (calendar.getTimeInMillis() / 1000L);
        Log.d(LOGa, "UnixCurrentTime: " + unixCurrentTime);

        //Find the reminders that are currently active
        ReminderHelper reminderHelper = new ReminderHelper(context);
        reminderHelper.getActiveReminders();
        List<Reminder> reminders = reminderHelper.getActiveReminders();

        ArrayList results = new ArrayList();

        if (!reminders.isEmpty()) {
            for (Reminder reminder : reminders) {
                cListItem reminderData = new cListItem();
                reminderData.setUnixTime(reminder.getUnixtime());
                reminderData.setType(reminder.getType());
                reminderData.setRepeat(reminder.getRepeatfreq());
                reminderData.setDesc(reminder.getDescription());
                reminderData.setId(reminder.getId());
                results.add(reminderData);
            }
        }
        return results;
    }

    public void showDesc(int position) {
        try {
            Object o = listView.getItemAtPosition(position);
            cListItem reminderData = (cListItem) o;
            Toast.makeText(ViewRemindersListActivity.this, reminderData.getDesc().toString(), Toast.LENGTH_LONG).show();
        } catch (NullPointerException npe) {
            Log.e(LOGa, "" + npe.toString());
            Toast.makeText(ViewRemindersListActivity.this, "There was a problem getting the description, please try again", Toast.LENGTH_LONG).show();
        }
    }

    public void deleteItem(int position) {
        try {
            //Get object at position
            Object o = listView.getItemAtPosition(position);
            long reminderId = ((cListItem) o).getId();

            removeAndUnscheduleReminder(reminderId);

            Toast.makeText(ViewRemindersListActivity.this, "Reminder Deleted", Toast.LENGTH_SHORT).show();

        } catch (NullPointerException npe) {
            Log.e(LOGa, "" + npe.toString());
            Toast.makeText(ViewRemindersListActivity.this, "There was a problem deleting the reminder, please try again", Toast.LENGTH_LONG).show();
        }

        activeRemindersList = getListData();
        cListAdapter listAdapterRefresh = new cListAdapter(this, activeRemindersList);
        listView.setAdapter(listAdapterRefresh);
    }

    private void removeAndUnscheduleReminder(long reminderId) {
        ReminderHelper reminderHelper = new ReminderHelper(context);
        //ScheduleHelper scheduleHelper = new ScheduleHelper(context);
        reminderHelper.softDeleteReminderWithId(reminderId);
        //scheduleHelper.unScheduleReminder(reminderId);
    }

    //TODO: Tracker code
//    @Override
//    public void onStart() {
//        super.onStart();
//        // The rest of your onStart() code.
//        EasyTracker.getInstance(this).activityStart(this);  // Add this method.
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        // The rest of your onStop() code.
//        EasyTracker.getInstance(this).activityStop(this);  // Add this method.
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
