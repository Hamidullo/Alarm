package com.shame.alarm.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.shame.alarm.R;
import com.shame.alarm.list.AlarmRecyclerViewAdapter;
import com.shame.alarm.list.AlarmsListViewModel;
import com.shame.alarm.list.OnToggleAlarmListener;
import com.shame.alarm.model.Alarm;

import java.util.ArrayList;
import java.util.List;

public class ListAlarmActivity extends AppCompatActivity implements OnToggleAlarmListener {
    private AlarmRecyclerViewAdapter alarmRecyclerViewAdapter;
    private AlarmsListViewModel alarmsListViewModel;
    private RecyclerView alarmsRecyclerView;
    private TextView text;
    private ArrayList alarmsList = new ArrayList<Alarm>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_alarm);

        alarmsRecyclerView = findViewById(R.id.list_alarm);
        alarmsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        text = findViewById(R.id.notAlarmList);

        alarmRecyclerViewAdapter = new AlarmRecyclerViewAdapter(this);
        alarmsListViewModel = ViewModelProviders.of(this).get(AlarmsListViewModel.class);
        alarmsListViewModel.getAlarmsLiveData().observe(this, new Observer<List<Alarm>>() {
            @Override
            public void onChanged(List<Alarm> alarms) {
                if (alarms != null) {
                    alarmRecyclerViewAdapter.setAlarms(alarms);
                    alarmsList = (ArrayList) alarms;

                    if (alarms.size() == 0){
                        alarmsRecyclerView.setVisibility(View.GONE);
                        text.setVisibility(View.VISIBLE);
                    } else {
                        alarmsRecyclerView.setVisibility(View.VISIBLE);
                        text.setVisibility(View.GONE);
                    }
                }
            }
        });

        alarmsRecyclerView.setAdapter(alarmRecyclerViewAdapter);

        alarmsRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        alarmRecyclerViewAdapter.setOnItemClickListener(new AlarmRecyclerViewAdapter.onClickListner() {
            @Override
            public void onItemClick(int position, View v) {
               /* startActivity(new Intent(ListAlarmActivity.this,SetAlarmActivity.class)
                        .putExtra("position",position));*/
            }

            @Override
            public void onItemLongClick(int position, View v) {

                Dialog dialog = new Dialog(ListAlarmActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.checkbox3);

                Button cancel = dialog.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                Button ok = dialog.findViewById(R.id.ok);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        alarmsListViewModel.delete((Alarm) alarmsList.get(position));

                        Toast.makeText(ListAlarmActivity.this,"Будильник ${alarms[position].title} удален!",Toast.LENGTH_SHORT).show();

                        alarmsListViewModel.getAlarmsLiveData().observe(ListAlarmActivity.this, new Observer<List<Alarm>>() {
                            @Override
                            public void onChanged(List<Alarm> alarms) {
                                if (alarms != null) {
                                    alarmRecyclerViewAdapter.setAlarms(alarms);
                                    alarmsList = (ArrayList) alarms;
                                }
                            }
                        });
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public void onToggle(Alarm alarm) {
        if (alarm.isStarted()) {
            alarm.cancelAlarm(getApplicationContext());
        } else {
            alarm.schedule(getApplicationContext());
        }
        alarmsListViewModel.update(alarm);
    }
}