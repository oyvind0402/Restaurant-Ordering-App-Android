package com.example.mappe2_s188886_s344046.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class SMSBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, PeriodicalService.class);
        context.startService(i);
    }
}
