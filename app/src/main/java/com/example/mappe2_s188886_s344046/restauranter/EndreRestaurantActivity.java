package com.example.mappe2_s188886_s344046.restauranter;

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

import com.example.mappe2_s188886_s344046.ForsideActivity;
import com.example.mappe2_s188886_s344046.R;
import com.example.mappe2_s188886_s344046.settings.SettingsActivity;
import com.example.mappe2_s188886_s344046.utils.DBHandler;

import java.util.regex.Pattern;

public class EndreRestaurantActivity extends AppCompatActivity {
    DBHandler db;
    long restaurantId;
    EditText innEndreNavn, innEndreAdresse, innEndreTelefon, innEndreType;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.endrerestaurant_layout);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);

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
            if(validerInput()) {
                Restaurant restaurant = new Restaurant();
                restaurant.setId(restaurantId);
                restaurant.setNavn(innEndreNavn.getText().toString());
                restaurant.setAdresse(innEndreAdresse.getText().toString());
                restaurant.setTelefon(innEndreTelefon.getText().toString());
                restaurant.setType(innEndreType.getText().toString());
                db.oppdaterRestaurant(restaurant);
                Toast.makeText(this, restaurant.getNavn() + " oppdatert!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Feil input, pr??v igjen.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Fyll inn alle feltene", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validerInput() {
        Pattern telefonPattern = Pattern.compile("[0-9 \\-()+]{5,25}");
        Pattern navnPattern = Pattern.compile("[a-zA-Z???????????? \\-.]{2,50}");
        Pattern adressePattern = Pattern.compile("[0-9a-zA-Z????????????. \\-]{2,50}");
        Pattern typePattern = Pattern.compile("[0-9a-zA-Z????????????. \\-]{2,50}");

        return telefonPattern.matcher(innEndreTelefon.getText().toString()).matches()
                && navnPattern.matcher(innEndreNavn.getText().toString()).matches()
                && adressePattern.matcher(innEndreAdresse.getText().toString()).matches()
                && typePattern.matcher(innEndreType.getText().toString()).matches();
    }

    public void tilForside(View view) {
        Intent intent = new Intent(this, ForsideActivity.class);
        //Legger til CLEAR_TOP intent flag for ?? fjerne alt p?? callstacken.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void tilSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, AlleRestauranterActivity.class);
        startActivity(intent);
        finish();
    }
}
