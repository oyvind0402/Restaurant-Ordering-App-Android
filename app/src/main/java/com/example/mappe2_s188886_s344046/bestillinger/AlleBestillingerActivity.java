package com.example.mappe2_s188886_s344046.bestillinger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mappe2_s188886_s344046.settings.SettingsActivity;
import com.example.mappe2_s188886_s344046.utils.DBHandler;
import com.example.mappe2_s188886_s344046.R;
import com.example.mappe2_s188886_s344046.utils.Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AlleBestillingerActivity extends AppCompatActivity {
    DBHandler db;
    ListView aktiveBestillinger;
    TextView tilinaktiveBestillinger;
    Bestilling bestilling;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allebestillinger_layout);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.inflateMenu(R.menu.menu);
        setSupportActionBar(myToolbar);
        tilinaktiveBestillinger = (TextView) findViewById(R.id.til_inaktive) ;

        db = new DBHandler(this);
        aktiveBestillinger = (ListView) findViewById(R.id.aktiveBestillinger);

        populateBestilling();
    }

    public void populateBestilling(){
        List<Bestilling> allebestillinger = db.finnALleBestillinger();
        List<Bestilling> aktiveBestillingerList = new ArrayList<>();
        List<Bestilling> inaktiveBestillingerList = new ArrayList<>();

        Utilities.populateBestillingList(db, allebestillinger, aktiveBestillingerList, inaktiveBestillingerList);
        Utilities.populateBestillingListView(this, db, aktiveBestillinger, aktiveBestillingerList, R.layout.simple_list_item_2_single_choice);
        aktiveBestillinger.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        aktiveBestillinger.setOnItemClickListener((parent, view, position, id) -> {
            HashMap<String, String> hm = (HashMap<String, String>) aktiveBestillinger.getItemAtPosition(position);
            try {
                long bestillingId = Utilities.extractId(hm.get("title"));
                bestilling = db.finnBestilling(bestillingId);
            } catch (Exception e) {
                Toast.makeText(this, "Feil ved henting av bestilling data", Toast.LENGTH_LONG).show();
                bestilling = null;
                aktiveBestillinger.setItemChecked(position, false);
            }
        });

        if (inaktiveBestillingerList.size() > 0 ){
            tilinaktiveBestillinger.setVisibility(View.VISIBLE);
        } else {
            tilinaktiveBestillinger.setVisibility(View.GONE);
        }
    }

    public void lagreBestilling(View view) {
        Intent intent = new Intent(this, LagreBestillingActivity.class);
        startActivity(intent);
        finish();
    }

    public void endreBestilling(View view){
        if (bestilling != null) {
            Intent intent = new Intent(this, EndreBestillingActivity.class);
            Bundle bundle = new Bundle();
            bundle.putLong("bestillingId", bestilling.getId());
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }  else {
            Toast.makeText(this, "Må velge en bestilling", Toast.LENGTH_SHORT).show();
        }
    }

    public void slettBestilling(View view){
        if (bestilling != null) {
            new AlertDialog.Builder(this).setTitle("Sletting av bestilling #" + bestilling.getId()).setMessage("Er du sikker på at du vil slette bestilling #" + bestilling.getId() + "?").setPositiveButton("Ja", (dialogInterface, i) -> {
                db.slettBestilling(bestilling.getId());
                Intent intent = new Intent(getApplicationContext(), AlleBestillingerActivity.class);
                startActivity(intent);
                finish();
            }).setNegativeButton("Nei", (dialogInterface, i) -> Toast.makeText(getApplicationContext(), "Sletting av bestilling #" + bestilling.getId() + " ikke vellykket.", Toast.LENGTH_SHORT).show()).create().show();
        } else {
            Toast.makeText(this, "Du må velge en bestilling for å slette", Toast.LENGTH_SHORT).show();
        }
    }

    public void tilInaktive(View view){
        Intent intent = new Intent(this, InaktiveBestillingerActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.settings_activity) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }
        return true;
    }
}
