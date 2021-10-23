package com.example.mappe2_s188886_s344046;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class BestillingActivity extends AppCompatActivity {
    private Spinner spinner;
    private DBHandler db;
    private long restaurantid;
    EditText innDato, innTidspunkt;
    ListView listView;
    SimpleCursorAdapter simpleCursorAdapter;
    CheckboxCursorAdapter checkboxCursorAdapter;
    Cursor cursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bestilling_layout);
        innDato = (EditText) findViewById(R.id.innDato);
        innTidspunkt = (EditText) findViewById(R.id.innTidspunkt);
        spinner = (Spinner) findViewById(R.id.restaurant_dropdown);
        db = new DBHandler(getApplicationContext());
        populateSpinner();
        populateFriendList();
    }

    public void populateSpinner() {
        List<Restaurant> restaurantListe = db.finnAlleRestauranter();
        List<String> liste = new ArrayList<>();
        for (int i = 0; i < restaurantListe.size(); i++) {
            liste.add(restaurantListe.get(i).getNavn());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(BestillingActivity.this, android.R.layout.simple_spinner_item, liste);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                restaurantid = Long.valueOf(i + 1);
                Log.d("TAG", String.valueOf(restaurantid));
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
        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.venner_listview_layout, cursor, dataFieldValues, dataFields);
        listView.setAdapter(simpleCursorAdapter);
    }

    public void bestillRestaurant(View view) {
        List<Venn> venneListe = new ArrayList<>();
        int[] dataFields = new int[] {
                R.id.venn_navn,
                R.id.venn_telefon
        };
        String[] dataFieldValues = new String[] {
                "navn",
                "telefon"
        };
        checkboxCursorAdapter = new CheckboxCursorAdapter(this, R.layout.venner_listview_layout, cursor, dataFieldValues, dataFields, R.id.venn_checkbox);
        long[] vennIdListe = checkboxCursorAdapter.getCheckedVennIdList();
        //TODO sjekke at alt dette funker :D
        for (long l : vennIdListe) {
            Venn venn = db.finnVenn(l);
            venneListe.add(venn);
        }
        Bestilling bestilling = new Bestilling(restaurantid, innDato.getText().toString(), innTidspunkt.getText().toString(), venneListe);
        db.leggTilBestilling(bestilling);
        Toast.makeText(this, "Bestilling av bord hos " + spinner.getSelectedItem() + " bekreftet.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bestilling_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.frontpage_activity) {
            Intent i = new Intent(this, ForsideActivity.class);
            startActivity(i);
        } else if(item.getItemId() == R.id.restaurant_activity) {
            Intent i2 = new Intent(this, RestaurantActivity.class);
            startActivity(i2);
        } else if(item.getItemId() == R.id.friend_activity) {
            Intent i3 = new Intent(this, VennActivity.class);
            startActivity(i3);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
