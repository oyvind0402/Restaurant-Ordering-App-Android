package com.example.mappe2_s188886_s344046.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import java.util.Calendar;
import java.util.Locale;


public class PeriodicalService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        Intent i = new Intent(this, SMSService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, i, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //Skaffer tidspunktet for å starte servicen, default verdi 17:00
        String tidspunkt = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("velgTidspunkt", "17:00");
        String[] tid = tidspunkt.split(":");
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tid[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(tid[1]));
        //Servicen startes en gang om dagen
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }
}
