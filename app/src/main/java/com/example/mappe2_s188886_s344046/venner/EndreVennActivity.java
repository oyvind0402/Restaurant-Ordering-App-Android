package com.example.mappe2_s188886_s344046.venner;

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

public class EndreVennActivity extends AppCompatActivity {
    DBHandler db;
    long vennId;
    EditText innEndreNavn, innEndreTelefon;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.endrevenn_layout);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);

        db = new DBHandler(this);
        innEndreNavn = (EditText) findViewById(R.id.innEndreVennNavn);
        innEndreTelefon = (EditText) findViewById(R.id.innEndreVennTelefon);

        Bundle bundle = getIntent().getExtras();
        vennId = bundle.getLong("vennId");
        Venn venn = db.finnVenn(vennId);

        innEndreNavn.setText(venn.getNavn());
        innEndreTelefon.setText(venn.getTelefon());
    }

    public void endreVenn(View view) {
        if(!innEndreTelefon.getText().toString().isEmpty() && !innEndreNavn.getText().toString().isEmpty()) {
            if(validerInput()) {
                Venn venn = new Venn();
                venn.setId(vennId);
                venn.setNavn(innEndreNavn.getText().toString());
                venn.setTelefon(innEndreTelefon.getText().toString());
                db.oppdaterVenn(venn);
                Toast.makeText(this, venn.getNavn() + " oppdatert!", Toast.LENGTH_SHORT).show();
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

        return telefonPattern.matcher(innEndreTelefon.getText().toString()).matches()
                && navnPattern.matcher(innEndreNavn.getText().toString()).matches();
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
        Intent intent = new Intent(this, AlleVennerActivity.class);
        startActivity(intent);
        finish();
    }
}
