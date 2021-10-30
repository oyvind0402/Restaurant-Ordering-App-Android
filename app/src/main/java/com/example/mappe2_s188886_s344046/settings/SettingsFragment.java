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

            boolean pushIsActivated = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getBoolean("notifikasjon", false);
            boolean smsIsActivated = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getBoolean("sms", false);
            //Starter servicen på nytt hvis brukeren endrer tidspunkt for sending av sms og notifikasjon
            if(pushIsActivated || smsIsActivated) {
                startService();
            }
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
        String melding = sharedPreferences.getString("smsMelding", "Du har en bestilling i dag!");
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
        boolean pushIsActivated = sharedPreferences.getBoolean("notifikasjon", false);
        boolean smsIsActivated = sharedPreferences.getBoolean("sms", false);
        if(key.equals("notifikasjon")) {
            //Hvis sms er av og push akkurat ble satt til true - restarter vi service:
            if(!smsIsActivated && pushIsActivated) {
                startService();
            }
            //Hvis push akkurat ble satt til false og sms er av - slår vi av service:
            if(!pushIsActivated && !smsIsActivated) {
                stoppService();
            }
        }
        if(key.equals("sms")) {
            //Hvis push er av og sms akkurat ble slått på - restarter vi service:
            if(!pushIsActivated && smsIsActivated) {
                startService();
            }
            //Hvis push er av og sms akkurat ble satt til av - slår vi av service:
            if(!smsIsActivated && !pushIsActivated) {
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
