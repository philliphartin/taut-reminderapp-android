package ulster.serg.tautreminderapp.view.activity;

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

import java.util.ArrayList;
import java.util.List;

import ulster.serg.tautreminderapp.controller.helpers.AnalyticsHelper;
import ulster.serg.tautreminderapp.controller.helpers.ReminderHelper;
import ulster.serg.tautreminderapp.controller.listviewadapter.ListAdapterForReminders;
import ulster.serg.tautreminderapp.controller.listviewadapter.ListItem;
import ulster.serg.tautreminderapp.model.sugarorm.Reminder;

/**
 * Created by Phillip J Hartin on 21/10/13.
 */
public class ViewRemindersListActivity extends Activity {
    private static final String LOGa = "ViewRemindersListActivity";
    public int selectedItem = -1;
    protected Object mActionMode;
    public ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(ulster.serg.tautreminderapp.R.menu.pressmenu_listitem, menu);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case ulster.serg.tautreminderapp.R.id.menuitem_delete:
                    deleteItem(selectedItem);
                    mode.finish();
                    return true;
                case ulster.serg.tautreminderapp.R.id.menuitem_desc:
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
    private ListView listView;
    private ListAdapterForReminders listAdapter;
    private ArrayList activeRemindersList;
    private AnalyticsHelper analytics;
    private Context context = this;
    private Toast mToast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ulster.serg.tautreminderapp.R.layout.activity_reminder_list);

        analytics = new AnalyticsHelper(this);
        analytics.track_screenView("View Reminders");

        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        //Setup Actionbar
        ActionBar actionBar = getActionBar();
        actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle("View All Reminders");

        //List Setup
        activeRemindersList = getListData();
        listView = (ListView) findViewById(ulster.serg.tautreminderapp.R.id.custom_list);
        listAdapter = new ListAdapterForReminders(this, activeRemindersList);
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
        //Find the reminders that are currently active
        ReminderHelper reminderHelper = new ReminderHelper(context);
        reminderHelper.getActiveReminders();
        List<Reminder> reminders = reminderHelper.getActiveReminders();

        ArrayList results = new ArrayList();

        if (!reminders.isEmpty()) {
            for (Reminder reminder : reminders) {
                ListItem reminderData = new ListItem();
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
            ListItem reminderData = (ListItem) o;
            mToast.setText(reminderData.getDesc().toString());

        } catch (NullPointerException npe) {
            Log.e(LOGa, "" + npe.toString());
            mToast.setText("There was a problem getting the description, please try again");
        }

        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }

    public void deleteItem(int position) {
        try {
            //Get object at position
            Object o = listView.getItemAtPosition(position);
            long reminderId = ((ListItem) o).getId();
            removeAndUnscheduleReminder(reminderId);
            mToast.setText("Reminder Deleted");

        } catch (NullPointerException npe) {
            Log.e(LOGa, "" + npe.toString());
            mToast.setText("There was a problem deleting the reminder, please try again");
        }

        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();

        activeRemindersList = getListData();
        ListAdapterForReminders listAdapterRefresh = new ListAdapterForReminders(this, activeRemindersList);
        listView.setAdapter(listAdapterRefresh);
    }

    private void removeAndUnscheduleReminder(long reminderId) {
        ReminderHelper reminderHelper = new ReminderHelper(context);
        String reminderType = reminderHelper.getReminderTypeFromId((int) reminderId);
        analytics.track_deleteReminder(reminderType);

        reminderHelper.softDeleteReminderWithId(reminderId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
