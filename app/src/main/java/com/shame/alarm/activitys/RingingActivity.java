package com.shame.alarm.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.shame.alarm.R;
import com.shame.alarm.service.AlarmService;

public class RingingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringing);
    }
    public void cancel(View view) {
        Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
        getApplicationContext().stopService(intentService);
        finish();
    }

    public void snooze(View view) {

    }

}