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

public class LagreVennActivity extends AppCompatActivity {
    EditText innNavn, innTelefon;
    DBHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.venn_layout);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.inflateMenu(R.menu.venn_menu);
        setSupportActionBar(myToolbar);

        innNavn = (EditText) findViewById(R.id.innVennNavn);
        innTelefon = (EditText) findViewById(R.id.innVennTelefon);
        db = new DBHandler(this);
    }

    public void lagreVenn(View v) {
        Venn venn = new Venn(innNavn.getText().toString(), innTelefon.getText().toString());
        if(!venn.getNavn().isEmpty() && !venn.getTelefon().isEmpty()) {
            db.leggTilVenn(venn);
            Toast.makeText(this, "Lagret " + innNavn.getText().toString() + " som venn!", Toast.LENGTH_SHORT).show();
            resetInput();
        }
    }

    public void resetInput() {
        innNavn.setText("");
        innTelefon.setText("");
    }


}
