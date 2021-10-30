package com.example.mappe2_s188886_s344046.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.example.mappe2_s188886_s344046.services.PeriodicalService;


public class SMSBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean pushIsActivated = sharedPreferences.getBoolean("notifikasjon", false);
        boolean smsIsActivated = sharedPreferences.getBoolean("sms", false);

        if(smsIsActivated || pushIsActivated) {
            Intent i = new Intent(context, PeriodicalService.class);
            context.startService(i);
        }
    }
}
