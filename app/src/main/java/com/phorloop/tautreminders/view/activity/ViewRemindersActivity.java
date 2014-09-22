package com.phorloop.tautreminders.view.activity;

import android.app.Activity;
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
import com.phorloop.tautreminders.controller.ListViewAdapter.ReminderListAdapter;
import com.phorloop.tautreminders.controller.ListViewAdapter.ReminderListItem;
import com.phorloop.tautreminders.model.sugarorm.Reminder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Phillip J Hartin on 21/10/13.
 */
public class ViewRemindersActivity extends Activity {
    private static final String LOGa = "ViewRemindersActivity";


    public int selectedItem = -1;
    public ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        // called when the action mode is created; startActionMode() was called
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            // assumes that you have "contexual.xml" menu resources
            inflater.inflate(R.menu.pressmenu_listitem, menu);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // called when the user selects a contextual menu item
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menuitem_delete:
                    deleteItem(selectedItem);
                    // the Action was executed, close the CAB
                    mode.finish();
                    return true;
                case R.id.menuitem_desc:
                    showDesc(selectedItem);
                    // the Action was executed, close the CAB
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
    ReminderListAdapter listAdapter;
    ArrayList activeRemindersList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_list);

        //List Setup
        activeRemindersList = getListData();
        listView = (ListView) findViewById(R.id.custom_list);
        listAdapter = new ReminderListAdapter(this, activeRemindersList);
        listView.setAdapter(listAdapter);

        //Button Methods
        //SHORT CLICK
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                Log.d(LOGa, "Selected Item position: " + position);
                Log.d(LOGa, "Selected Item id: " + id);

                selectedItem = position;
                // start the CAB using the ActionMode.Callback defined above
                mActionMode = ViewRemindersActivity.this
                        .startActionMode(mActionModeCallback);
                v.setSelected(true);

                //Object o = listView.getItemAtPosition(position);
                //ListItemReminder reminderData = (ListItemReminder) o;
                //Crouton.makeText(ActivityReminderList.this, "Long Press for more options", Style.INFO).show();
            }
        });

        //LONG click
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                if (mActionMode != null) {
                    return false;
                }

                selectedItem = position;
                // start the CAB using the ActionMode.Callback defined above
                mActionMode = ViewRemindersActivity.this
                        .startActionMode(mActionModeCallback);
                view.setSelected(true);
                return true;
            }
        });
    }

    private ArrayList getListData() {
        Calendar calendar = Calendar.getInstance();
        //GET CURRENT DATE AS UNIX TIME
        long unixCurrentTime = (calendar.getTimeInMillis() / 1000L);
        Log.d(LOGa, "UnixCurrentTime: " + unixCurrentTime);

        //Find the reminders that are currently active
        List<Reminder> reminders = Reminder.find(Reminder.class, "active = ?", "1");

        ArrayList results = new ArrayList();
        for (Reminder reminder : reminders) {

            ReminderListItem reminderData = new ReminderListItem();
            reminderData.setType(reminder.getType());
            reminderData.setTime(reminder.getTime());
            reminderData.setDate(reminder.getDate());
            reminderData.setRepeat(reminder.getRepeatfreq());
            reminderData.setDesc(reminder.getDescription());
            reminderData.setId(reminder.getId());
            results.add(reminderData);
        }

        //adds items to list
        return results;
    }

    public void showDesc(int position) {
        try {
            Object o = listView.getItemAtPosition(position);
            ReminderListItem reminderData = (ReminderListItem) o;
            Toast.makeText(ViewRemindersActivity.this, reminderData.getDesc().toString() + " at " + reminderData.getTime().toString(), Toast.LENGTH_LONG).show();
        } catch (NullPointerException npe) {
            Log.e(LOGa, "" + npe.toString());
            Toast.makeText(ViewRemindersActivity.this, "There was a problem getting the description, please try again", Toast.LENGTH_LONG).show();
        }
    }

    public void deleteItem(int position) {
        try {
            Object o = listView.getItemAtPosition(position);
            ReminderListItem reminderData = (ReminderListItem) o;
            //db.deleteReminder(reminderData.getId());

            Reminder reminder = Reminder.findById(Reminder.class, reminderData.getId());
            reminder.delete();

            //FIXME: Delete reminder fix
            Toast.makeText(ViewRemindersActivity.this, "Reminder Deleted", Toast.LENGTH_LONG).show();
        } catch (NullPointerException npe) {
            Log.e(LOGa, "" + npe.toString());
            Toast.makeText(ViewRemindersActivity.this, "There was a problem deleting the reminder, please try again", Toast.LENGTH_LONG).show();
        }

        activeRemindersList = getListData();
        ReminderListAdapter listAdapterRefresh = new ReminderListAdapter(this, activeRemindersList);
        listView.setAdapter(listAdapterRefresh);

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
