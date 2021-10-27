package com.example.mappe2_s188886_s344046.bestillinger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mappe2_s188886_s344046.R;
import com.example.mappe2_s188886_s344046.settings.SettingsActivity;
import com.example.mappe2_s188886_s344046.utils.DBHandler;
import com.example.mappe2_s188886_s344046.utils.Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class InaktiveBestillingerActivity extends AppCompatActivity {
    DBHandler db;
    ListView inaktiveBestillinger;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inaktivebestillinger_layout);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.inflateMenu(R.menu.menu);
        setSupportActionBar(myToolbar);

        db = new DBHandler(this);
        inaktiveBestillinger = (ListView) findViewById(R.id.inaktiveBestillinger);

        populateBestilling();
    }

    public void populateBestilling(){
        List<Bestilling> allebestillinger = db.finnALleBestillinger();
        List<Bestilling> inaktiveBestillingerList = new ArrayList<>();

        for (Bestilling bestilling: allebestillinger) {
            try {
                Calendar dagensDato = Calendar.getInstance(Locale.getDefault());
                Calendar valgtDato = Calendar.getInstance(Locale.getDefault());
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
                valgtDato.setTime(Objects.requireNonNull(df.parse(bestilling.getDato() + " " + bestilling.getTidspunkt())));

                if (valgtDato.before(dagensDato)){
                    inaktiveBestillingerList.add(bestilling);
                } else {
                    if (db.finnRestaurant(bestilling.getRestaurantid()).getNavn() == null){
                        inaktiveBestillingerList.add(bestilling);
                    }
                }
            } catch (NullPointerException | ParseException e) {
                Log.w("BESTILLING_ERR", "Kunne ikke parse en bestilling");
            }
        }

        Utilities.populateListView(this, db, inaktiveBestillinger, inaktiveBestillingerList, R.layout.simple_list_item_2_multiple_choice);
        inaktiveBestillinger.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        inaktiveBestillinger.setOnItemClickListener((parent, view, position, id) -> {
            Toast.makeText(this, String.format("Position: %d, ID: %d", position, id), Toast.LENGTH_LONG).show();
        });

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AlleBestillingerActivity.class);
        startActivity(intent);
        finish();
    }

}
