package com.example.mappe2_s188886_s344046.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mappe2_s188886_s344046.ForsideActivity;
import com.example.mappe2_s188886_s344046.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        getSupportFragmentManager().beginTransaction().replace(R.id.settings_container, new SettingsFragment()).commit();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ForsideActivity.class);
        startActivity(intent);
        finish();
    }
}