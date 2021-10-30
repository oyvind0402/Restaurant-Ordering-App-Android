package com.example.mappe2_s188886_s344046.venner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mappe2_s188886_s344046.ForsideActivity;
import com.example.mappe2_s188886_s344046.settings.SettingsActivity;
import com.example.mappe2_s188886_s344046.utils.DBHandler;
import com.example.mappe2_s188886_s344046.R;

import java.util.regex.Pattern;

public class LagreVennActivity extends AppCompatActivity {
    EditText innNavn, innTelefon;
    DBHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lagrevenn_layout);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);

        innNavn = (EditText) findViewById(R.id.innVennNavn);
        innTelefon = (EditText) findViewById(R.id.innVennTelefon);
        db = new DBHandler(this);
    }

    public void lagreVenn(View v) {
        Venn venn = new Venn(innNavn.getText().toString(), innTelefon.getText().toString());
        if(!venn.getNavn().isEmpty() && !venn.getTelefon().isEmpty()) {
            if(validerInput()) {
                if(db.finnesVenn(venn)) {
                    Toast.makeText(this, "Venn finnes allerede!", Toast.LENGTH_SHORT).show();
                } else {
                    db.leggTilVenn(venn);
                    Toast.makeText(this, "Lagret " + innNavn.getText().toString() + " som venn!", Toast.LENGTH_SHORT).show();
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

        return telefonPattern.matcher(innTelefon.getText().toString()).matches()
                && navnPattern.matcher(innNavn.getText().toString()).matches();
    }

    public void resetInput() {
        innNavn.setText("");
        innTelefon.setText("");
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
