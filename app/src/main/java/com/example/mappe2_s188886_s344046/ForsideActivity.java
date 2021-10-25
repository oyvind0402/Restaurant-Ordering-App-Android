package com.example.mappe2_s188886_s344046;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.mappe2_s188886_s344046.bestillinger.AlleBestillingerActivity;
import com.example.mappe2_s188886_s344046.restauranter.AlleRestauranterActivity;
import com.example.mappe2_s188886_s344046.settings.SettingsActivity;
import com.example.mappe2_s188886_s344046.venner.AlleVennerActivity;

public class ForsideActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "42";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forside_layout);

        createNotificationChannel();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.inflateMenu(R.menu.menu);
        setSupportActionBar(myToolbar);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "SMSNotifChannel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.settings_activity) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }
        return true;
    }

    public void visRestauranter(View view) {
        Intent intent = new Intent(this, AlleRestauranterActivity.class);
        startActivity(intent);
    }

    public void visVenner(View view) {
        Intent intent = new Intent(this, AlleVennerActivity.class);
        startActivity(intent);
    }

    public void visSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void visBestillinger(View view) {
        Intent intent = new Intent(this, AlleBestillingerActivity.class);
        startActivity(intent);
    }
}