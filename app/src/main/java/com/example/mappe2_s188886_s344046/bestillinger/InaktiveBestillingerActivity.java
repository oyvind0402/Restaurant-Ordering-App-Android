package com.example.mappe2_s188886_s344046.bestillinger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mappe2_s188886_s344046.ForsideActivity;
import com.example.mappe2_s188886_s344046.R;
import com.example.mappe2_s188886_s344046.settings.SettingsActivity;
import com.example.mappe2_s188886_s344046.utils.DBHandler;
import com.example.mappe2_s188886_s344046.utils.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class InaktiveBestillingerActivity extends AppCompatActivity {
    DBHandler db;
    ListView inaktiveBestillinger;
    boolean[] valgteIndekser;
    int antallValgte = 0;
    Button slettInaktive;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inaktivebestillinger_layout);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);

        slettInaktive = (Button) findViewById(R.id.slett_inaktive_btn);

        db = new DBHandler(this);
        inaktiveBestillinger = (ListView) findViewById(R.id.inaktiveBestillinger);

        populateBestilling();
    }

    public void populateBestilling(){
        List<Bestilling> allebestillinger = db.finnALleBestillinger();
        List<Bestilling> inaktiveBestillingerList = new ArrayList<>();

        Utilities.populateBestillingList(db, allebestillinger, null, inaktiveBestillingerList);
        if (inaktiveBestillingerList.size() > 0) {
            int blue = getResources().getColor(R.color.blue_logo);
            slettInaktive.setEnabled(true);
            slettInaktive.setBackgroundColor(blue);
            Utilities.populateBestillingListView(this, db, inaktiveBestillinger, inaktiveBestillingerList, R.layout.simple_list_item_2_multiple_choice);
            valgteIndekser = new boolean[inaktiveBestillingerList.size()];
            Arrays.fill(valgteIndekser, false);

            inaktiveBestillinger.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            inaktiveBestillinger.setOnItemClickListener((parent, view, position, id) -> {
                valgteIndekser[position] = !valgteIndekser[position];
                if ((valgteIndekser[position])) {
                    antallValgte++;
                } else {
                    antallValgte--;
                }
            });
        } else {
            List<String> placeholderListe = new ArrayList<>();
            placeholderListe.add("Ingen inaktive bestillinger for øyeblikket!");
            int grey = getResources().getColor(R.color.gray_logo);
            slettInaktive.setEnabled(false);
            slettInaktive.setBackgroundColor(grey);
            ArrayAdapter<String> placeholderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, placeholderListe);
            inaktiveBestillinger.setAdapter(placeholderAdapter);
        }

    }

    public void slettBestillinger(View view){
        if (antallValgte > 0) {
            new AlertDialog.Builder(this).setTitle("Sletting av " + antallValgte + " bestilling(er)").setMessage("Er du sikker på at du vil slette de valgte bestillinger?").setPositiveButton("Ja", (dialogInterface, i) -> {
                for (int j = 0; j < valgteIndekser.length; j++){
                    if (valgteIndekser[j]) {
                        HashMap<String, String> hm = (HashMap<String, String>) inaktiveBestillinger.getItemAtPosition(j);
                        try {
                            db.slettBestilling(Utilities.extractId(hm.get("item")));
                        } catch (Exception e) {
                            Log.w("BESTILLING_SLETT_ERR", "Kunne ikke finne bestilling ID");
                        }
                    }
                }
                Intent intent = new Intent(getApplicationContext(), InaktiveBestillingerActivity.class);
                startActivity(intent);
                finish();
            }).setNegativeButton("Nei", (dialogInterface, i) -> Toast.makeText(getApplicationContext(), "Sletting av " + antallValgte + " bestilling(er) avbrutt.", Toast.LENGTH_SHORT).show()).create().show();
        } else {
                Toast.makeText(this, "Velg en eller flere bestillinger", Toast.LENGTH_SHORT).show();
        }
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
        Intent intent = new Intent(this, AlleBestillingerActivity.class);
        startActivity(intent);
        finish();
    }

}
