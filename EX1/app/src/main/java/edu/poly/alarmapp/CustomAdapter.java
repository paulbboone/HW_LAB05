package edu.poly.alarmapp;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.List;

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private List<Alarm> alarmList;
    private LayoutInflater layoutInflater;

    public CustomAdapter(Context c, List<Alarm> alarmList) {
        this.context = c;
        this.alarmList = alarmList;
        layoutInflater = (LayoutInflater.from(context));
    }


    @Override
    public int getCount() {
        return alarmList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        convertView = layoutInflater.inflate(R.layout.item, null);
        final Alarm selectedAlarm = alarmList.get(position);
        final TextView nameTV = convertView.findViewById(R.id.nameTextView);
        final TextView alarmTV = convertView.findViewById(R.id.timeTextView);
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        nameTV.setText(selectedAlarm.getName());
        alarmTV.setText(selectedAlarm.toString());

        final Intent serviceIntent = new Intent(context,Receiver.class);

        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, selectedAlarm.getHour());
        calendar.set(Calendar.MINUTE, selectedAlarm.getMinute());
        calendar.set(Calendar.SECOND, 0);
        if(calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DATE, 1);
        }
        ToggleButton toggleButton = convertView.findViewById(R.id.toggle);
        toggleButton.setChecked(selectedAlarm.getStatus());
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectedAlarm.setStatus(isChecked);
                DatabaseHelp db = new DatabaseHelp(context);
                db.updateAlarm(selectedAlarm);
                MainActivity.alarmList.clear();
                List<Alarm> list = db.getAllAlarms();
                MainActivity.alarmList.addAll(list);
                notifyDataSetChanged();

                if(!isChecked && selectedAlarm.toString().equals(MainActivity.activeAlarm)) {
                    serviceIntent.putExtra("extra", "off");
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, position, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(pendingIntent);
                    context.sendBroadcast(serviceIntent);
                }
            }
        });

        if(selectedAlarm.getStatus()) {
            serviceIntent.putExtra("extra", "on");
            serviceIntent.putExtra("active", selectedAlarm.toString());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, position, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        return convertView;
    }
}