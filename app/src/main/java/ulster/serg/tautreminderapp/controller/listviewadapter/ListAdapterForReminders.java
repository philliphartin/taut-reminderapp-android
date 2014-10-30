package ulster.serg.tautreminderapp.controller.listviewadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ulster.serg.tautreminderapp.controller.helpers.DateHelper;

import java.util.ArrayList;

/**
 * Created by Phillip J Hartin on 21/10/13.
 */
public class ListAdapterForReminders extends BaseAdapter {

    private ArrayList listData;
    private LayoutInflater layoutInflater;

    public ListAdapterForReminders(Context context, ArrayList listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(ulster.serg.tautreminderapp.R.layout.list_reminderlist_item, null);
            holder = new ViewHolder();
            holder.typeView = (TextView) convertView.findViewById(ulster.serg.tautreminderapp.R.id.reminderlist_textview_type);
            holder.dateView = (TextView) convertView.findViewById(ulster.serg.tautreminderapp.R.id.reminderlist_textview_date);
            holder.timeView = (TextView) convertView.findViewById(ulster.serg.tautreminderapp.R.id.reminderlist_textview_time);
            holder.repeatView = (TextView) convertView.findViewById(ulster.serg.tautreminderapp.R.id.reminderlist_textview_repeat);
            holder.icon = (ImageView) convertView.findViewById(ulster.serg.tautreminderapp.R.id.reminderlist_item_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ListItem reminderItem = (ListItem) listData.get(position);

        //Get UnixTime to convert for human readable formats
        long unixTime = reminderItem.getUnixTime();

        DateHelper dateHelper = new DateHelper();
        String date = dateHelper.getDateHumanReadableFromUnixTime(unixTime);
        String time = dateHelper.getTimeHumanReadableFromUnixTime(unixTime);

        holder.typeView.setText(reminderItem.getType());
        holder.dateView.setText(date);
        holder.timeView.setText(time);
        holder.repeatView.setText("Repeat: " + reminderItem.getRepeat());


        String type = reminderItem.getType().toString();

        try {
            if ("Medication".equals(type)) {
                holder.icon.setImageResource(ulster.serg.tautreminderapp.R.drawable.medication);
            } else if ("Appointment".equals(type)) {
                holder.icon.setImageResource(ulster.serg.tautreminderapp.R.drawable.clock);
            } else if ("Meal".equals(type)) {
                holder.icon.setImageResource(ulster.serg.tautreminderapp.R.drawable.teachers_day);
            } else if ("Personal Hygiene".equals(type)) {
                holder.icon.setImageResource(ulster.serg.tautreminderapp.R.drawable.hygiene);
            } else if ("Drink".equals(type)) {
                holder.icon.setImageResource(ulster.serg.tautreminderapp.R.drawable.plastic_bottle);
            } else if ("Charge Phone".equals(type)) {
                holder.icon.setImageResource(ulster.serg.tautreminderapp.R.drawable.charge);
            } else if ("Other".equals(type)) {
                holder.icon.setImageResource(ulster.serg.tautreminderapp.R.drawable.info);
            }
        } catch (Exception e) {
        }

        return convertView;
    }

    static class ViewHolder {
        TextView typeView;
        TextView dateView;
        TextView timeView;
        TextView repeatView;
        ImageView icon;
    }


}
