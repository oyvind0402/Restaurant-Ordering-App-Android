package com.example.mappe2_s188886_s344046;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        getSupportFragmentManager().beginTransaction().replace(R.id.settings_container, new SettingsFragment()).commit();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, FrontPageActivity.class);
        startActivity(intent);
        finish();
    }
}
