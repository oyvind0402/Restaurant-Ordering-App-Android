package com.example.mappe2_s188886_s344046.bestillinger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Map;
import java.util.Objects;

public class AlleBestillingerActivity extends AppCompatActivity {
    DBHandler db;
    ListView aktiveBestillinger;
    TextView tilinaktiveBestillinger;

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
                    } else {
                        aktiveBestillingerList.add(bestilling);
                    }
                }
            } catch (NullPointerException | ParseException e) {
                Log.w("BESTILLING_ERR", "Kunne ikke parse en bestilling");
            }
        }
        Utilities.populateListView(this, db, aktiveBestillinger, aktiveBestillingerList, R.layout.simple_list_item_2_single_choice);
        aktiveBestillinger.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        aktiveBestillinger.setOnItemClickListener((parent, view, position, id) -> {
            //TODO Handle checkbox not enabling
            //TODO Handle action
//          CheckedTextView btn = (CheckedTextView) view.getTag(R.id.text2);
//            btn.setChecked(!btn.isChecked());
//            HashMap<String, String> hm = (HashMap<String, String>) aktiveBestillinger.getItemAtPosition(position);
//
//            Log.w("ANA", aktiveBestillinger.getTag(android.R.id.text2) + "");
//            aktiveBestillinger.setItemChecked(position, true);
////            Toast.makeText(this, "" + parent.getTag().getClass().toString(), Toast.LENGTH_LONG).show();
////            Toast.makeText(this, hm.get("item"), Toast.LENGTH_LONG).show();
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
