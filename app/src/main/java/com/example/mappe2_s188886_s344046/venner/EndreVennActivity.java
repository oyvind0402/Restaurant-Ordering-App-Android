package com.example.mappe2_s188886_s344046.venner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mappe2_s188886_s344046.R;
import com.example.mappe2_s188886_s344046.utils.DBHandler;

public class EndreVennActivity extends AppCompatActivity {
    DBHandler db;
    long vennId;
    EditText innEndreNavn, innEndreTelefon;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.endrevenn_layout);

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
            Venn venn = new Venn();
            venn.set_id(vennId);
            venn.setNavn(innEndreNavn.getText().toString());
            venn.setTelefon(innEndreTelefon.getText().toString());
            db.oppdaterVenn(venn);
            Toast.makeText(this, venn.getNavn() + " oppdatert!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Du må skrive inn både navn og telefon for å oppdatere!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AlleVennerActivity.class);
        startActivity(intent);
        finish();
    }
}
