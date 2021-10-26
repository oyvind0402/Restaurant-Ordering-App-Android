package com.example.mappe2_s188886_s344046.bestillinger;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mappe2_s188886_s344046.R;
import com.example.mappe2_s188886_s344046.restauranter.Restaurant;
import com.example.mappe2_s188886_s344046.utils.DBHandler;
import com.example.mappe2_s188886_s344046.venner.Venn;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EndreBestillingActivity extends AppCompatActivity {
    DBHandler db;
    long bestillingId;
    Spinner innEndreRestaurant;
    ListView innEndreVenner;
    EditText innEndreDato, innEndreTidspunkt;
    List<Venn> venneListe;
    Bestilling bestilling;
    TimePickerDialog.OnTimeSetListener tidspunktDialogLytter;
    DatePickerDialog.OnDateSetListener datoDialogLytter;
    Calendar tidspunktKalender;
    Calendar datoKalender;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.endrebestilling_layout);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.inflateMenu(R.menu.menu);
        setSupportActionBar(myToolbar);

        db = new DBHandler(this);

        innEndreRestaurant = (Spinner) findViewById(R.id.innEndreBestillingRestaurant_dropdown);
        innEndreVenner = (ListView) findViewById(R.id.innEndreBestillingVenneListe);
        innEndreDato = (EditText) findViewById(R.id.innEndreBestillingDato);
        innEndreTidspunkt = (EditText) findViewById(R.id.innEndreBestillingTidspunkt);
        innEndreVenner.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        Bundle bundle = getIntent().getExtras();
        bestillingId = bundle.getLong("bestillingId");
        bestilling = db.finnBestilling(bestillingId);
        lasteVenner();
        lasteRestaurant();
        innEndreDato.setText(bestilling.getDato());
        innEndreTidspunkt.setText(bestilling.getTidspunkt());

        tidspunktKalender = Calendar.getInstance();
        datoKalender = Calendar.getInstance();

        tidspunktDialogLytter = new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                DecimalFormat format = new DecimalFormat("00");
                String minuttFormat = format.format(minute);
                String timeFormat = format.format(hourOfDay);
                innEndreTidspunkt.setText(timeFormat + ":" + minuttFormat);
            }
        };

        innEndreTidspunkt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int time = tidspunktKalender.get(Calendar.HOUR_OF_DAY);
                int minutt = tidspunktKalender.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(EndreBestillingActivity.this, tidspunktDialogLytter, time, minutt, true);
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", dialog);
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Avslutt", dialog);
                dialog.show();
            }
        });

        datoDialogLytter = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                oppdaterDato(year, month, day);
            }
        };

        innEndreDato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(EndreBestillingActivity.this, datoDialogLytter, datoKalender.get(Calendar.YEAR), datoKalender.get(Calendar.MONTH), datoKalender.get(Calendar.DAY_OF_MONTH));
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", dialog);
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Avslutt", dialog);
                dialog.show();
            }
        });

    }

    public void oppdaterDato(int year, int month, int day) {
        datoKalender.set(Calendar.YEAR, year);
        datoKalender.set(Calendar.MONTH, month);
        datoKalender.set(Calendar.DAY_OF_MONTH, day);
        String format = "dd-MM-yyyy";
        SimpleDateFormat datoFormat = new SimpleDateFormat(format, Locale.getDefault());

        innEndreDato.setText(datoFormat.format(datoKalender.getTime()));
    }

    public void endreBestilling(View view){
        if (!innEndreDato.getText().toString().isEmpty() && !innEndreTidspunkt.getText().toString().isEmpty()) {
            SparseBooleanArray checkedItemPositions = innEndreVenner.getCheckedItemPositions();
            venneListe.clear();
            for(int i = 0; i < checkedItemPositions.size(); i++) {
                if(checkedItemPositions.valueAt(i)) {
                    Venn venn = (Venn) innEndreVenner.getItemAtPosition(i);
                    venneListe.add(venn);
                }
            }
            Bestilling nyBestilling = new Bestilling();
            nyBestilling.setId(bestillingId);
            nyBestilling.setDato(innEndreDato.getText().toString());
            nyBestilling.setTidspunkt(innEndreTidspunkt.getText().toString());
            nyBestilling.setRestaurantid(bestilling.getRestaurantid());
            nyBestilling.setVenner(venneListe);
            db.oppdaterBestilling(nyBestilling);
            Toast.makeText(this, "Bestilling hos " + innEndreRestaurant.getSelectedItem() + " oppdatert!", Toast.LENGTH_SHORT).show();
            venneListe.clear();
        } else {
            Toast.makeText(this, "Du må fylle inn alle feltene for å oppdatere!", Toast.LENGTH_SHORT).show();
        }
    }

    public void lasteRestaurant(){
        int selectedRestaurant = -1;
        List<Restaurant> restaurantListe = db.finnAlleRestauranter();
        List<String> liste = new ArrayList<>();
        for (int i = 0; i < restaurantListe.size(); i++) {
            String restaurantNavn = restaurantListe.get(i).getNavn();
            String bestillingRestaurant = db.finnRestaurant(bestilling.getRestaurantid()).getNavn();
            liste.add(restaurantNavn);
            if (restaurantNavn.equals(bestillingRestaurant)){
                selectedRestaurant = i;
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(EndreBestillingActivity.this, android.R.layout.simple_spinner_item, liste);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        innEndreRestaurant.setAdapter(adapter);
        innEndreRestaurant.setSelection(selectedRestaurant);
        innEndreRestaurant.setEnabled(false);
    }

    public void lasteVenner() {
        boolean displayAlert = false;
        if (bestilling.getVenner() != null) {
            try {
                venneListe = bestilling.getVenner();
                List<Venn> DBVenneListe = db.finnAlleVenner();
                ArrayAdapter<Venn> vennAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, DBVenneListe);
                innEndreVenner.setAdapter(vennAdapter);

                for (int i = 0; i < vennAdapter.getCount(); i++) {
                    Venn curVenn = vennAdapter.getItem(i);
                    for(Venn venn: venneListe) {
                        if(curVenn.getNavn().equals(venn.getNavn())) {
                            innEndreVenner.setItemChecked(i, true);
                        }
                    }
                }
            } catch (NullPointerException e) {
                displayAlert = true;
            }


            if (displayAlert) {
                new AlertDialog.Builder(this)
                        .setTitle("Denne ordren inkluderer venner som har blitt slettet")
                        .setMessage("Hvis du fortsetter med å endre bestillingen, alle referanser til disse venner vil bli fjernet.")
                        .setPositiveButton(R.string.fortsett, null)
                        .setNegativeButton(R.string.avslutt, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AlleBestillingerActivity.class);
        startActivity(intent);
        finish();
    }
}
