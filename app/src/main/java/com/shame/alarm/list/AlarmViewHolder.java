package com.shame.alarm.list;

import static com.shame.alarm.list.AlarmRecyclerViewAdapter.onclicklistner;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shame.alarm.R;
import com.shame.alarm.model.Alarm;

import java.text.SimpleDateFormat;
import java.util.Date;


public class AlarmViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private TextView alarmTime;
    private ImageView alarmTypeOff;
    private TextView alarmRecurringDays;
    private TextView alarmTitle;

    Switch alarmStarted;

    private OnToggleAlarmListener listener;

    public AlarmViewHolder(@NonNull View itemView, OnToggleAlarmListener listener) {
        super(itemView);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);

        alarmTime = itemView.findViewById(R.id.item_alarm_time);
        alarmStarted = itemView.findViewById(R.id.item_alarm_started);
        alarmRecurringDays = itemView.findViewById(R.id.item_alarm_recurringDays);
        alarmTitle = itemView.findViewById(R.id.item_alarm_title);
        alarmTypeOff = itemView.findViewById(R.id.item_type_off);

        this.listener = listener;
    }

    public void bind(Alarm alarm) {
        String alarmText = String.format("%02d:%02d", alarm.getHour(), alarm.getMinute());

        alarmTime.setText(alarmText);
        alarmStarted.setChecked(alarm.isStarted());

        switch (alarm.getTypeOff()) {
            case "Встряска":
                alarmTypeOff.setImageResource(R.drawable.image6);

                break;
            case "Примеры":
                alarmTypeOff.setImageResource(R.drawable.image7);

                break;
            case "QR коде":
                alarmTypeOff.setImageResource(R.drawable.image8);
                break;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM HH:mm ");
        Date resultdate = new Date(alarm.getCreated());
        System.out.println(sdf.format(resultdate));

        if (alarm.getTitle().length() != 0) {
            //alarmTitle.setText(String.format(alarm.getTitle() + " | " + alarm.getAlarmId() + " | " + alarm.getCreated()));
            alarmTitle.setText(String.format("%s | %d | %s", alarm.getTitle(), alarm.getAlarmId(),
                    sdf.format(resultdate) ));
        } else {
            //alarmTitle.setText(String.format(alarm.getAlarmId() + " | " + alarm.getCreated()));
            alarmTitle.setText(String.format("%s | %d | %s", "Alarm", alarm.getAlarmId(),
                    sdf.format(resultdate) ));
        }
        alarmRecurringDays.setText(alarm.getRecurringDaysText());
        alarmStarted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listener.onToggle(alarm);
            }
        });
    }


    @Override
    public void onClick(View v) {
        onclicklistner.onItemClick(getAdapterPosition(), v);
    }

    @Override
    public boolean onLongClick(View v) {
        onclicklistner.onItemLongClick(getAdapterPosition(), v);
        return false;
    }
}
