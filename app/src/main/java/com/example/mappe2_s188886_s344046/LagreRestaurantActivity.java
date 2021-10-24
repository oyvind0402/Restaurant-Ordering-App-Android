package com.example.mappe2_s188886_s344046;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class LagreRestaurantActivity extends AppCompatActivity {
    EditText innNavn, innAdresse, innTelefon, innType;
    DBHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_layout);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.inflateMenu(R.menu.restaurant_menu);
        setSupportActionBar(myToolbar);

        innNavn = (EditText) findViewById(R.id.innNavn);
        innAdresse = (EditText) findViewById(R.id.innAdresse);
        innTelefon = (EditText) findViewById(R.id.innTelefon);
        innType = (EditText) findViewById(R.id.innType);
        db = new DBHandler(this);
    }

    public void lagreRestaurant(View v) {
        Restaurant restaurant = new Restaurant(innNavn.getText().toString(), innAdresse.getText().toString(), innTelefon.getText().toString(), innType.getText().toString());
        if(restaurant.getNavn() != null && restaurant.getAdresse() != null && restaurant.getTelefon() != null && restaurant.getType() != null) {
            db.leggTilRestaurant(restaurant);
            Toast.makeText(this, "Lagret " + innNavn.getText().toString() + " som restaurant!", Toast.LENGTH_SHORT).show();
            resetInput();
        }
    }

    public void resetInput() {
        innNavn.setText("");
        innAdresse.setText("");
        innTelefon.setText("");
        innType.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.restaurant_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.frontpage_activity) {
            Intent i = new Intent(this, ForsideActivity.class);
            startActivity(i);
        } else if(item.getItemId() == R.id.friend_activity) {
            Intent i2 = new Intent(this, LagreVennActivity.class);
            startActivity(i2);
        } else if(item.getItemId() == R.id.order_activity) {
            Intent i3 = new Intent(this, AlleBestillingerActivity.class);
            startActivity(i3);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
