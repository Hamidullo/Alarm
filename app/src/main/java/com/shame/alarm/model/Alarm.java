package com.shame.alarm.model;

import static com.shame.alarm.alarm.AlarmBroadcastReceiver.DIFFICULTY;
import static com.shame.alarm.alarm.AlarmBroadcastReceiver.FRIDAY;
import static com.shame.alarm.alarm.AlarmBroadcastReceiver.HOUR;
import static com.shame.alarm.alarm.AlarmBroadcastReceiver.MINUTE;
import static com.shame.alarm.alarm.AlarmBroadcastReceiver.MONDAY;
import static com.shame.alarm.alarm.AlarmBroadcastReceiver.RECURRING;
import static com.shame.alarm.alarm.AlarmBroadcastReceiver.RINGTONE;
import static com.shame.alarm.alarm.AlarmBroadcastReceiver.RINGTONETYPE;
import static com.shame.alarm.alarm.AlarmBroadcastReceiver.SATURDAY;
import static com.shame.alarm.alarm.AlarmBroadcastReceiver.SUNDAY;
import static com.shame.alarm.alarm.AlarmBroadcastReceiver.THURSDAY;
import static com.shame.alarm.alarm.AlarmBroadcastReceiver.TITLE;
import static com.shame.alarm.alarm.AlarmBroadcastReceiver.TUESDAY;
import static com.shame.alarm.alarm.AlarmBroadcastReceiver.TYPEOFF;
import static com.shame.alarm.alarm.AlarmBroadcastReceiver.VOLUME;
import static com.shame.alarm.alarm.AlarmBroadcastReceiver.WEDNESDAY;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.shame.alarm.alarm.AlarmBroadcastReceiver;

import java.util.Calendar;

@Entity(tableName = "alarm_table")
public class Alarm {
    @PrimaryKey
    @NonNull
    private int alarmId;

    private int hour, minute;
    private boolean started, recurring;
    private boolean monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    private String title,typeOff = "default",difficulty,ringtone,typeRingtone;
    private float volume;

    private long created;

    public Alarm(int alarmId, int hour, int minute, boolean started, boolean recurring,
                 boolean monday, boolean tuesday, boolean wednesday, boolean thursday,
                 boolean friday, boolean saturday, boolean sunday, String title, String typeOff,
                 String difficulty, String ringtone, String typeRingtone, float volume, long created) {
        this.alarmId = alarmId;
        this.hour = hour;
        this.minute = minute;
        this.started = started;
        this.recurring = recurring;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
        this.title = title;
        this.typeOff = typeOff;
        this.difficulty = difficulty;
        this.ringtone = ringtone;
        this.typeRingtone = typeRingtone;
        this.volume = volume;
        this.created = created;
    }

    public void schedule(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra(RECURRING, recurring);
        intent.putExtra(MONDAY, monday);
        intent.putExtra(TUESDAY, tuesday);
        intent.putExtra(WEDNESDAY, wednesday);
        intent.putExtra(THURSDAY, thursday);
        intent.putExtra(FRIDAY, friday);
        intent.putExtra(SATURDAY, saturday);
        intent.putExtra(SUNDAY, sunday);

        intent.putExtra(TITLE, title);
        intent.putExtra(TYPEOFF, typeOff);
        intent.putExtra(DIFFICULTY, difficulty);
        intent.putExtra(RINGTONE, ringtone);
        intent.putExtra(RINGTONETYPE, typeRingtone);
        intent.putExtra(VOLUME, volume);
        intent.putExtra(HOUR, hour);
        intent.putExtra(MINUTE, minute);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0);


        if (!recurring) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            System.out.println(hour);
            System.out.println(minute);
            System.out.println(calendar.get(Calendar.HOUR_OF_DAY));
            System.out.println(calendar.get(Calendar.MINUTE));
            String toastText = null;
            try {
                toastText = String.format("Одноразовый будильник %s запланировано в %02d:%02d", title, hour, minute, alarmId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();

            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    alarmPendingIntent
            );
        } else {
            Calendar calendar = Calendar.getInstance();
            //calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // if alarm time has already passed, increment day by 1
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
            }

            String toastText = String.format("Повторяющийся будильник %s запланировано на %s в %02d:%02d", title, getRecurringDaysText(), hour, minute, alarmId);
            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();

            final long RUN_DAILY = 24 * 60 * 60 * 1000;
            alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    RUN_DAILY,
                    alarmPendingIntent
            );
        }

        this.started = true;
    }

    public void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0);
        alarmManager.cancel(alarmPendingIntent);
        this.started = false;

        String toastText = String.format("Будильник отменена на %02d:%02d with id %d", hour, minute, alarmId);
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
        Log.i("cancel", toastText);
    }


    public String getRecurringDaysText() {
        if (!recurring) {
            return null;
        }

        String days = "";
        if (monday) {
            days += "Пн ";
        }
        if (tuesday) {
            days += "Вт ";
        }
        if (wednesday) {
            days += "Ср ";
        }
        if (thursday) {
            days += "Чт ";
        }
        if (friday) {
            days += "Пт ";
        }
        if (saturday) {
            days += "Сб ";
        }
        if (sunday) {
            days += "Вс ";
        }

        return days;
    }

    public int getAlarmId() {
        return alarmId;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public boolean isMonday() {
        return monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public String getTitle() {
        return title;
    }

    public String getTypeOff() {
        return typeOff;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getRingtone() {
        return ringtone;
    }

    public String getTypeRingtone() {
        return typeRingtone;
    }

    public float getVolume() {
        return volume;
    }

    public long getCreated() {
        return created;
    }
}
