package com.example.mappe2_s188886_s344046.bestillinger;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mappe2_s188886_s344046.utils.CheckboxCursorAdapter;
import com.example.mappe2_s188886_s344046.utils.DBHandler;
import com.example.mappe2_s188886_s344046.R;
import com.example.mappe2_s188886_s344046.restauranter.Restaurant;
import com.example.mappe2_s188886_s344046.venner.Venn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LagreBestillingActivity extends AppCompatActivity {
    private Spinner spinner;
    private DBHandler db;
    private long restaurantid;
    EditText innDato, innTidspunkt;
    ListView listView;
    SimpleCursorAdapter simpleCursorAdapter;
    CheckboxCursorAdapter checkboxCursorAdapter;
    Cursor cursor;
    List<Venn> venneListe;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lagrebestilling_layout);
        innDato = (EditText) findViewById(R.id.innDato);
        innTidspunkt = (EditText) findViewById(R.id.innTidspunkt);
        spinner = (Spinner) findViewById(R.id.restaurant_dropdown);
        db = new DBHandler(getApplicationContext());
        venneListe = new ArrayList<>();
        populateSpinner();
        populateFriendList();
    }

    public void populateSpinner() {
        List<Restaurant> restaurantListe = db.finnAlleRestauranter();

        // Valgte en Map + Liste løsning, slik at nøkkel-verdiene er alltid paret (ingen fare for unsync)
        Map<String, Long> map = new HashMap<>();
        List<String> liste = new ArrayList<>();
        for (int i = 0; i < restaurantListe.size(); i++) {
            map.put(restaurantListe.get(i).getNavn(), restaurantListe.get(i).getId());
            liste.add(restaurantListe.get(i).getNavn());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(LagreBestillingActivity.this, android.R.layout.simple_spinner_item, liste);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    restaurantid = map.get(liste.get(i));
                } catch (NullPointerException e) {
                    Toast.makeText(getApplicationContext(), "Feil ved å velge restaurant", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //Løsning basert på: https://stackoverflow.com/questions/47533503/how-to-make-a-checkbox-from-information-found-in-database/47540946
    public void populateFriendList() {
        cursor = db.finnVenner();
        listView = (ListView) findViewById(R.id.venneListe);
        int[] dataFields = new int[] {
                R.id.venn_navn,
                R.id.venn_telefon
        };
        String[] dataFieldValues = new String[] {
                "navn",
                "telefon"
        };
        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.bestillinger_vennerlistview_layout, cursor, dataFieldValues, dataFields);
        listView.setAdapter(simpleCursorAdapter);

        checkboxCursorAdapter = new CheckboxCursorAdapter(this, R.layout.bestillinger_vennerlistview_layout, cursor, dataFieldValues, dataFields, R.id.venn_checkbox);
        listView.setAdapter(checkboxCursorAdapter);
    }

    public void bestillRestaurant(View view) {
        long[] vennIdListe = checkboxCursorAdapter.getCheckedVennIdList();
        for (long l : vennIdListe) {
            Venn venn = db.finnVenn(l);
            venneListe.add(venn);
        }
        if(!innDato.getText().toString().isEmpty() && !innTidspunkt.getText().toString().isEmpty()) {
            Bestilling bestilling = new Bestilling(restaurantid, innDato.getText().toString(), innTidspunkt.getText().toString(), venneListe);
            db.leggTilBestilling(bestilling);
            Toast.makeText(this, "Bestilling av bord hos " + spinner.getSelectedItem() + " bekreftet.", Toast.LENGTH_SHORT).show();
            venneListe.clear();
        } else {
            Toast.makeText(this, "Du må velge både dato og tidspunkt!", Toast.LENGTH_SHORT).show();
        }
    }
}
