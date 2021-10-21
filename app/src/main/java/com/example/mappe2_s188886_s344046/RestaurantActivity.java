package com.example.mappe2_s188886_s344046;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RestaurantActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_layout);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);

        myToolbar.inflateMenu(R.menu.restaurant_menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.restaurant_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.frontpage_activity:
                Intent i = new Intent(this, FrontPageActivity.class);
                startActivity(i);
                break;
            case R.id.friend_activity:
                Intent i2 = new Intent(this, FriendActivity.class);
                startActivity(i2);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
