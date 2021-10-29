package com.example.mappe2_s188886_s344046.settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.mappe2_s188886_s344046.R;
import com.example.mappe2_s188886_s344046.services.SMSService;

import java.text.DecimalFormat;
import java.util.Calendar;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {
    Preference velgTidspunkt;
    TimePickerDialog.OnTimeSetListener tidspunktDialogLytter;
    EditTextPreference smsMelding;
    String tidspunkt = "";
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        velgTidspunkt = (Preference) findPreference("velgTidspunkt");
        assert velgTidspunkt != null;
        tidspunktDialogLytter = (timePicker, time, minutt) -> {
            DecimalFormat format = new DecimalFormat("00");
            String minuttFormat = format.format(minutt);
            String timeFormat = format.format(time);
            tidspunkt = String.format("%s:%s", timeFormat, minuttFormat);
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit();
            editor.putString("velgTidspunkt", tidspunkt);
            editor.apply();
        };
        velgTidspunkt.setOnPreferenceClickListener(preference -> {
            TimePickerDialog dialog = new TimePickerDialog(getContext(), tidspunktDialogLytter, Calendar.HOUR_OF_DAY, Calendar.MINUTE, true);
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", dialog);
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Avslutt", dialog);
            dialog.show();
            return false;
        });

        smsMelding = (EditTextPreference) findPreference("smsMelding");
        SharedPreferences sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String melding = sharedPreferences.getString("smsMelding", "");
        String tid = sharedPreferences.getString("velgTidspunkt", "17:00");
        smsMelding.setSummary(melding);
        velgTidspunkt.setSummary(tid);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("notifikasjon")) {
            boolean pushIsActivated = sharedPreferences.getBoolean("notifikasjon", false);
            if(pushIsActivated) {
                startService();
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
