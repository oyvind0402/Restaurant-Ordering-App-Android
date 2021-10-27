package com.example.mappe2_s188886_s344046.settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.mappe2_s188886_s344046.R;
import com.example.mappe2_s188886_s344046.services.SMSService;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("notifikasjon")) {
            String isActivated = sharedPreferences.getString("notifikasjon", "false");
            if(isActivated.equals("true")) {
                startService();
            }
            if(isActivated.equals("false")) {
                String smsIsActivated = sharedPreferences.getString("sms", "false");
                if (smsIsActivated.equals("true")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("sms", "false");
                    editor.apply();
                }
                stoppService();
            }
        }
        requireActivity().recreate();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    public void startService() {
        Intent intent = new Intent();
        intent.setAction("com.example.mappe2_s188886_s344046.broadcast");
        getContext().sendBroadcast(intent);
    }

    public void stoppService() {
        Intent i = new Intent(getContext(), SMSService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getContext(), 0, i, 0);
        AlarmManager alarm = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

        if(alarm != null) {
            alarm.cancel(pendingIntent);
        }
    }
}
