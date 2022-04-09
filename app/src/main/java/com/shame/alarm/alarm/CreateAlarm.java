package com.shame.alarm.alarm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.shame.alarm.model.Alarm;
import com.shame.alarm.model.AlarmRepository;

public class CreateAlarm extends AndroidViewModel {
    private AlarmRepository alarmRepository;

    public CreateAlarm(@NonNull Application application) {
        super(application);

        alarmRepository = new AlarmRepository(application);
    }

    public void insert(Alarm alarm) {
        alarmRepository.insert(alarm);
    }
}
