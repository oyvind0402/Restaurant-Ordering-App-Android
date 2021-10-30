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
import com.example.mappe2_s188886_s344046.bestillinger.LagreBestillingActivity;
import com.example.mappe2_s188886_s344046.settings.SettingsActivity;
import com.example.mappe2_s188886_s344046.utils.DBHandler;

import java.util.List;
import java.util.regex.Pattern;

public class LagreRestaurantActivity extends AppCompatActivity {
    EditText innNavn, innAdresse, innTelefon, innType;
    DBHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lagrerestaurant_layout);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);

        innNavn = (EditText) findViewById(R.id.innNavn);
        innAdresse = (EditText) findViewById(R.id.innAdresse);
        innTelefon = (EditText) findViewById(R.id.innTelefon);
        innType = (EditText) findViewById(R.id.innType);
        db = new DBHandler(this);
    }

    public void lagreRestaurant(View v) {
        Restaurant restaurant = new Restaurant(innNavn.getText().toString(), innAdresse.getText().toString(), innTelefon.getText().toString(), innType.getText().toString());
        if(!restaurant.getNavn().isEmpty() && !restaurant.getAdresse().isEmpty() && !restaurant.getTelefon().isEmpty() && !restaurant.getType().isEmpty()) {
            if(validerInput()) {
                if(db.finnesRestaurant(restaurant)) {
                    Toast.makeText(this, "Restaurant finnes allerede!", Toast.LENGTH_LONG).show();
                } else {
                    db.leggTilRestaurant(restaurant);
                    Toast.makeText(this, "Lagret " + innNavn.getText().toString() + " som restaurant!", Toast.LENGTH_SHORT).show();
                    resetInput();
                }
            } else {
                Toast.makeText(this, "Feil input, prøv igjen!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Fyll inn alle feltene!", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validerInput() {
        Pattern telefonPattern = Pattern.compile("[0-9 \\-()+]{5,25}");
        Pattern navnPattern = Pattern.compile("[a-zA-ZÆØÅæøå \\-.]{2,50}");
        Pattern adressePattern = Pattern.compile("[0-9a-zA-ZøæåØÆÅ. \\-]{2,50}");
        Pattern typePattern = Pattern.compile("[0-9a-zA-ZøæåØÆÅ. \\-]{2,50}");

        return telefonPattern.matcher(innTelefon.getText().toString()).matches()
                && navnPattern.matcher(innNavn.getText().toString()).matches()
                && adressePattern.matcher(innAdresse.getText().toString()).matches()
                && typePattern.matcher(innType.getText().toString()).matches();
    }

    public void resetInput() {
        innNavn.setText("");
        innAdresse.setText("");
        innTelefon.setText("");
        innType.setText("");
    }

    public void tilForside(View view) {
        Intent intent = new Intent(this, ForsideActivity.class);
        //Legger til CLEAR_TOP intent flag for å fjerne alt på callstacken.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void tilSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        try {
            String tilbakeTil = getIntent().getExtras().getString("tilbaketil");
            if (tilbakeTil.equals("LagreBestilling")) {
                intent = new Intent(this, LagreBestillingActivity.class);
            } else {
                intent = new Intent(this, AlleRestauranterActivity.class);
            }
        } catch (NullPointerException e) {
            intent = new Intent(this, AlleRestauranterActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
