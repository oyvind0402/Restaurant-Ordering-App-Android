package com.example.mappe2_s188886_s344046.bestillinger;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mappe2_s188886_s344046.R;
import com.example.mappe2_s188886_s344046.restauranter.Restaurant;
import com.example.mappe2_s188886_s344046.settings.SettingsActivity;
import com.example.mappe2_s188886_s344046.utils.DBHandler;
import com.example.mappe2_s188886_s344046.utils.Utilities;
import com.example.mappe2_s188886_s344046.venner.Venn;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LagreBestillingActivity extends AppCompatActivity {
    private Spinner spinner;
    private DBHandler db;
    private long restaurantid;
    EditText innDato, innTidspunkt;
    ListView listView;
    List<Venn> venneListe;
    TimePickerDialog.OnTimeSetListener tidspunktDialogLytter;
    DatePickerDialog.OnDateSetListener datoDialogLytter;
    Calendar tidspunktKalender;
    Calendar datoKalender;
    Toast toastMessage;
    List<String> liste;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lagrebestilling_layout);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.inflateMenu(R.menu.menu);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);

        innDato = (EditText) findViewById(R.id.innDato);
        innTidspunkt = (EditText) findViewById(R.id.innTidspunkt);
        innTidspunkt.setHint("Dato må velges først");

        spinner = (Spinner) findViewById(R.id.restaurant_dropdown);
        db = new DBHandler(getApplicationContext());
        venneListe = new ArrayList<>();
        populateSpinner();

        listView = (ListView) findViewById(R.id.venneListe);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        populateFriendList();

        tidspunktKalender = Calendar.getInstance();
        datoKalender = Calendar.getInstance();

        datoDialogLytter = (view, year, month, day) -> oppdaterDato(year, month, day);

        innDato.setOnClickListener(view -> {
            DatePickerDialog dialog = new DatePickerDialog(LagreBestillingActivity.this, datoDialogLytter, datoKalender.get(Calendar.YEAR), datoKalender.get(Calendar.MONTH), datoKalender.get(Calendar.DAY_OF_MONTH));
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", dialog);
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Avslutt", dialog);
            Calendar dagensDato = Calendar.getInstance(Locale.getDefault());
            dialog.getDatePicker().setMinDate(dagensDato.getTimeInMillis());
            dialog.setTitle("");
            dialog.show();
        });
    }

    public void oppdaterDato(int year, int month, int day) {
        datoKalender.set(Calendar.YEAR, year);
        datoKalender.set(Calendar.MONTH, month);
        datoKalender.set(Calendar.DAY_OF_MONTH, day);
        String format = "dd-MM-yyyy";
        SimpleDateFormat datoFormat = new SimpleDateFormat(format, Locale.getDefault());

        innDato.setText(datoFormat.format(datoKalender.getTime()));
        aktivereTid(year, month, day);
    }

    public void aktivereTid(int year, int month, int day) {
        innTidspunkt.setHint("Tidspunkt");

        tidspunktDialogLytter = (view, hourOfDay, minute) -> {
            Calendar dagensDato = Calendar.getInstance(Locale.getDefault());
            Calendar valgtDato = Calendar.getInstance(Locale.getDefault());
            Utilities.formatDate(valgtDato, year, month, day, hourOfDay, minute);

            if (valgtDato.after(dagensDato)){
                DecimalFormat format = new DecimalFormat("00");
                String minuttFormat = format.format(minute);
                String timeFormat = format.format(hourOfDay);
                innTidspunkt.setText(String.format("%s:%s", timeFormat, minuttFormat));
            } else {
                innTidspunkt.setText("");
                Toast.makeText(this, "Valgt tid må være i fremtiden", Toast.LENGTH_LONG).show();
                innTidspunkt.setHint("Tidspunkt i FREMTIDEN");
            }
        };

        innTidspunkt.setOnClickListener(view -> {
            int time = tidspunktKalender.get(Calendar.HOUR_OF_DAY);
            int minutt = tidspunktKalender.get(Calendar.MINUTE);
            TimePickerDialog dialog = new TimePickerDialog(LagreBestillingActivity.this, tidspunktDialogLytter, time, minutt, true);
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", dialog);
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Avslutt", dialog);
            dialog.show();
        });
    }

    public void populateSpinner() {
        List<Restaurant> restaurantListe = db.finnAlleRestauranter();

        // Valgte en Map + Liste løsning, slik at nøkkel-verdiene er alltid paret (ingen fare for unsync)
        Map<String, Long> map = new HashMap<>();
        liste = new ArrayList<>();
        for (int i = 0; i < restaurantListe.size(); i++) {
            map.put(restaurantListe.get(i).getNavn(), restaurantListe.get(i).getId());
            liste.add(restaurantListe.get(i).getNavn());
        }
        liste.add("Velg Restaurant");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(LagreBestillingActivity.this, android.R.layout.simple_spinner_item, liste) {
            @Override
            public int getCount() {
                //Gjør så den siste verdien i listen ikke vises i dropdownlisten etter man har valgt noe annet
                return liste.size() - 1;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    //Hvis det ikke er placeholder verdien så initialiserer vi noen variabler
                    if(spinner.getSelectedItemPosition() != liste.size() - 1) {
                        restaurantid = map.get(liste.get(i));
                        Restaurant restaurant = db.finnRestaurant(restaurantid);

                        if(toastMessage != null) {
                            toastMessage.cancel();
                        }

                        toastMessage = Toast.makeText(getApplicationContext(), restaurant.toString(), Toast.LENGTH_LONG);
                        toastMessage.show();
                    }
                } catch (NullPointerException e) {
                    Toast.makeText(getApplicationContext(), "Feil ved å velge restaurant", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //Setter Velg Restaurant som første valg i dropdown'en
        spinner.setSelection(liste.size() - 1);
    }

    public void populateFriendList() {
        List<Venn> venneListe2 = db.finnAlleVenner();
        ArrayAdapter<Venn> vennAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, venneListe2);
        listView.setAdapter(vennAdapter);
    }

    public void lagreBestilling(View view) {
        SparseBooleanArray checkedItemPositions = listView.getCheckedItemPositions();
        for (int i = 0; i < checkedItemPositions.size(); i++) {
            if(checkedItemPositions.valueAt(i)) {
                Venn venn = (Venn) listView.getItemAtPosition(i);
                venneListe.add(venn);
            }
        }

        if(!innDato.getText().toString().isEmpty() && !innTidspunkt.getText().toString().isEmpty() && spinner.getSelectedItemPosition() != liste.size() -1) {
            Bestilling bestilling = new Bestilling(restaurantid, innDato.getText().toString(), innTidspunkt.getText().toString(), venneListe);
            db.leggTilBestilling(bestilling);
            Toast.makeText(this, "Bestilling av bord hos " + spinner.getSelectedItem() + " bekreftet.", Toast.LENGTH_SHORT).show();
            venneListe.clear();
        } else {
            Toast.makeText(this, "Du må velge dato, tidspunkt og restaurant!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AlleBestillingerActivity.class);
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
