package com.example.mappe2_s188886_s344046;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class FrontPageActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "42";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frontpage_layout);

        createNotificationChannel();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.inflateMenu(R.menu.front_menu);
        setSupportActionBar(myToolbar);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String isActivated = sharedPreferences.getString("notifikasjon", "false");
        if(isActivated.equals("true")) {
            startService();
        }
        if(isActivated.equals("false")) {
            stoppService();
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("defaultSmsMessage", "Du har en restaurantbestilling i dag, trykk her for å se dine bestillinger!");
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.front_menu, menu);
        return true;
    }

    public void startService() {
        Intent intent = new Intent();
        intent.setAction("com.example.mappe2_s188886_s344046.mybroadcast");
        sendBroadcast(intent);
    }

    public void stoppService() {
        Intent i = new Intent(this, SMSService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, i, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if(alarm != null) {
            alarm.cancel(pendingIntent);
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.restaurant_activity) {
            Intent i = new Intent(this, RestaurantActivity.class);
            startActivity(i);
        } else if(item.getItemId() == R.id.friend_activity) {
            Intent i2 = new Intent(this, FriendActivity.class);
            startActivity(i2);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void visSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }
}