package com.example.mappe2_s188886_s344046.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.Calendar;

public class PeriodicalService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        java.util.Calendar cal = Calendar.getInstance();
        Intent i = new Intent(this, SMSService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, i, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //Once every day the SMSService gets started
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }
}
