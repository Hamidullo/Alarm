package com.shame.alarm.service;

import static com.shame.alarm.alarm.AlarmBroadcastReceiver.DIFFICULTY;
import static com.shame.alarm.alarm.AlarmBroadcastReceiver.HOUR;
import static com.shame.alarm.alarm.AlarmBroadcastReceiver.MINUTE;
import static com.shame.alarm.alarm.AlarmBroadcastReceiver.RINGTONE;
import static com.shame.alarm.alarm.AlarmBroadcastReceiver.RINGTONETYPE;
import static com.shame.alarm.alarm.AlarmBroadcastReceiver.TITLE;
import static com.shame.alarm.alarm.AlarmBroadcastReceiver.TYPEOFF;
import static com.shame.alarm.alarm.AlarmBroadcastReceiver.VOLUME;
import static com.shame.alarm.app.App.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.shame.alarm.R;
import com.shame.alarm.activitys.MathActivity;
import com.shame.alarm.activitys.QRCodeActivity;
import com.shame.alarm.activitys.RingingActivity;
import com.shame.alarm.activitys.ShakeActivity;

import java.io.IOException;

public class AlarmService extends Service {
    private MediaPlayer mediaPlayer1,mediaPlayer2;
    private Vibrator vibrator;
    Intent notificationReceive;

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer2 = new MediaPlayer();
        mediaPlayer1 = MediaPlayer.create(this, R.raw.alarm);
        mediaPlayer1.setLooping(true);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

       //= new Intent(this, RingingActivity.class);

        switch (intent.getStringExtra(TYPEOFF)) {
            case "Встряска":
                notificationReceive = new Intent(this, ShakeActivity.class)
                        .putExtra(RINGTONE,intent.getStringExtra(RINGTONE));
                //Intent notificationReceive = new Intent(this, ShakeActivity.class);


                break;
            case "Примеры":
                notificationReceive = new Intent(this, MathActivity.class)
                        .putExtra(DIFFICULTY,intent.getStringExtra(DIFFICULTY))
                        .putExtra(RINGTONE,intent.getStringExtra(RINGTONE));
                //Intent notificationReceive = new Intent(this, ShakeActivity.class);


                break;
            case "QR коде":
                notificationReceive = new Intent(this, QRCodeActivity.class)
                        .putExtra(RINGTONE,intent.getStringExtra(RINGTONE));
                //Intent notificationReceive = new Intent(this, ShakeActivity.class);


                break;
            default:
                notificationReceive = new Intent(this, RingingActivity.class)
                        .putExtra(RINGTONE,intent.getStringExtra(RINGTONE));
                //Intent notificationReceive = new Intent(this, ShakeActivity.class);

                break;
        }

        PendingIntent pendingIntentReceive = PendingIntent.
                getActivity(this, 0, notificationReceive, 0);

        String alarmTitle = String.format("%s Будильник", intent.getStringExtra(TITLE));


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(alarmTitle)
                .setContentText(String.format("%02d:%02d Пора вставать!", intent.getIntExtra(HOUR,12),
                        intent.getIntExtra(MINUTE,12)))
                .setSmallIcon(R.drawable.image2)
                .setContentIntent(pendingIntentReceive)
                .build();


        if (intent.getStringExtra(RINGTONETYPE).contains("vibration")
                && intent.getStringExtra(RINGTONETYPE).matches("ringtone")){

            if (!intent.getStringExtra(RINGTONE).equals("default")){
                Uri uri = Uri.parse(intent.getStringExtra(RINGTONE));
                try {
                    mediaPlayer2.setDataSource(this, uri);
                    mediaPlayer2.setVolume(intent.getFloatExtra(VOLUME,100f),intent.getFloatExtra(VOLUME,100f));
                    mediaPlayer2.prepare();
                    mediaPlayer2.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                mediaPlayer1.start();
            }

            System.out.println("ringtone,    vibration    ");

            long[] pattern = { 0, 100, 1000 };
            vibrator.vibrate(pattern, 0);

        } else if (intent.getStringExtra(RINGTONETYPE).contains("vibration")) {
            System.out.println("    vibration    ");

            long[] pattern = { 0, 100, 1000 };
            vibrator.vibrate(pattern, 0);

        } else if (intent.getStringExtra(RINGTONETYPE).contains("ringtone")){

            System.out.println("ringtone    ");

            if (!intent.getStringExtra(RINGTONE).equals("default")){
                Uri uri = Uri.parse(intent.getStringExtra(RINGTONE));
                try {
                    mediaPlayer2.setDataSource(this, uri);
                    mediaPlayer2.setVolume(intent.getFloatExtra(VOLUME,100f),intent.getFloatExtra(VOLUME,100f));
                    mediaPlayer2.prepare();
                    mediaPlayer2.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                mediaPlayer1.start();
            }

        } else {

            System.out.println("errorrr ");

            if (!intent.getStringExtra(RINGTONE).equals("default")){
                Uri uri = Uri.parse(intent.getStringExtra(RINGTONE));
                try {
                    mediaPlayer2.setDataSource(this, uri);
                    mediaPlayer2.setVolume(intent.getFloatExtra(VOLUME,100f),intent.getFloatExtra(VOLUME,100f));
                    mediaPlayer2.prepare();
                    mediaPlayer2.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                mediaPlayer1.start();
            }


            long[] pattern = { 0, 100, 1000 };
            vibrator.vibrate(pattern, 0);

        }


        System.out.println(intent.getStringExtra(RINGTONE));

        startForeground(1,notification );

        startActivity();

        return START_STICKY;
    }

    private void startActivity(){
        notificationReceive.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(notificationReceive);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mediaPlayer1.stop();
        mediaPlayer2.stop();
        vibrator.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
