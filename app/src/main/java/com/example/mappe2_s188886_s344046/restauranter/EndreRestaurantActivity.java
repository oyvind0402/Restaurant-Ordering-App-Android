package com.example.mappe2_s188886_s344046.restauranter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mappe2_s188886_s344046.R;
import com.example.mappe2_s188886_s344046.utils.DBHandler;

public class EndreRestaurantActivity extends AppCompatActivity {
    DBHandler db;
    long restaurantId;
    EditText innEndreNavn, innEndreAdresse, innEndreTelefon, innEndreType;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.endrerestaurant_layout);

        db = new DBHandler(this);
        innEndreNavn = (EditText) findViewById(R.id.innEndreRestaurantNavn);
        innEndreAdresse = (EditText) findViewById(R.id.innEndreRestaurantAdresse);
        innEndreTelefon = (EditText) findViewById(R.id.innEndreRestaurantTelefon);
        innEndreType = (EditText) findViewById(R.id.innEndreRestaurantType);

        Bundle bundle = getIntent().getExtras();
        restaurantId = bundle.getLong("restaurantId");
        Restaurant restaurant = db.finnRestaurant(restaurantId);

        innEndreNavn.setText(restaurant.getNavn());
        innEndreAdresse.setText(restaurant.getAdresse());
        innEndreTelefon.setText(restaurant.getTelefon());
        innEndreType.setText(restaurant.getType());
    }

    public void endreRestaurant(View view){
        if (!innEndreNavn.getText().toString().isEmpty() &&
                !innEndreAdresse.getText().toString().isEmpty() &&
                !innEndreTelefon.getText().toString().isEmpty() &&
                !innEndreType.getText().toString().isEmpty()
        ) {
            Restaurant restaurant = new Restaurant();
            restaurant.setId(restaurantId);
            restaurant.setNavn(innEndreNavn.getText().toString());
            restaurant.setAdresse(innEndreAdresse.getText().toString());
            restaurant.setTelefon(innEndreTelefon.getText().toString());
            restaurant.setType(innEndreType.getText().toString());
            db.oppdaterRestaurant(restaurant);
            Toast.makeText(this, restaurant.getNavn() + " oppdatert!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Du må fylle inn alle feltene for å oppdatere!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, AlleRestauranterActivity.class);
        startActivity(intent);
        finish();
    }
}
