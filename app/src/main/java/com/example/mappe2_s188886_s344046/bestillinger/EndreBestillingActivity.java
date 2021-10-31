package com.example.mappe2_s188886_s344046.bestillinger;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mappe2_s188886_s344046.ForsideActivity;
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
import java.util.Date;
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
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);

        db = new DBHandler(this);

        innEndreRestaurant = (Spinner) findViewById(R.id.innEndreBestillingRestaurant_dropdown);
        innEndreVenner = (ListView) findViewById(R.id.innEndreBestillingVenneListe);
        innEndreDato = (EditText) findViewById(R.id.innEndreBestillingDato);
        innEndreTidspunkt = (EditText) findViewById(R.id.innEndreBestillingTidspunkt);
        innEndreVenner.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        //Finner verdiene fra bestillingen som ble valgt i allebestillinger:
        Bundle bundle = getIntent().getExtras();
        bestillingId = bundle.getLong("bestillingId");
        bestilling = db.finnBestilling(bestillingId);
        lasteVenner();
        lasteRestaurant();
        innEndreDato.setText(bestilling.getDato());
        innEndreTidspunkt.setText(bestilling.getTidspunkt());

        tidspunktKalender = Calendar.getInstance();
        datoKalender = Calendar.getInstance();

        //En lytter for dialogboksen for dato som setter datoen som blir valgt som teksten til edittextviewet.
        //Den fjerner også verdien i tidspunkt edittextviewet, fordi man kan ha byttet til dagens dato og hvis tidspunktet er i fortiden plutselig så gjør vi slik at brukeren må velge tidspunktet på nytt:
        datoDialogLytter = (view, year, month, day) -> oppdaterDato(year, month, day);

        //En lytter for edittextviewet for dato - en DatePickerDialog blir lagd og kjørt:
        innEndreDato.setOnClickListener(view -> {
            DatePickerDialog dialog = new DatePickerDialog(EndreBestillingActivity.this, datoDialogLytter, datoKalender.get(Calendar.YEAR), datoKalender.get(Calendar.MONTH), datoKalender.get(Calendar.DAY_OF_MONTH));
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", dialog);
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Avslutt", dialog);
            Date dagensDato = new Date();
            dialog.getDatePicker().setMinDate(dagensDato.getTime());
            dialog.setTitle("");
            dialog.show();
        });

        //Lytteren for når TimePickerDialog boksen får satt et tidspunkt, den setter tidspunktet i edittextviewet til det tidspunktet:
        tidspunktDialogLytter = (view, hourOfDay, minute) -> {
            String[] valgt = innEndreDato.getText().toString().split("-");
            try {
                int day = Integer.parseInt(valgt[0]);
                int month = Integer.parseInt(valgt[1]);
                int year = Integer.parseInt(valgt[2]);

                Calendar dagensDato = Calendar.getInstance(Locale.getDefault());
                Calendar valgtDato = Calendar.getInstance(Locale.getDefault());
                Utilities.formatDate(valgtDato, year, month - 1, day, hourOfDay, minute);

                if (valgtDato.after(dagensDato)) {
                    DecimalFormat format = new DecimalFormat("00");
                    String minuttFormat = format.format(minute);
                    String timeFormat = format.format(hourOfDay);
                    innEndreTidspunkt.setText(String.format("%s:%s", timeFormat, minuttFormat));
                } else {
                    innEndreTidspunkt.setText("");
                    Toast.makeText(this, "Valgt tid må være i fremtiden", Toast.LENGTH_LONG).show();
                    innEndreTidspunkt.setHint("Tidspunkt i FREMTIDEN");
                }
            } catch (NumberFormatException e){
                innEndreTidspunkt.setText("");
                Toast.makeText(this, "Feil med dato", Toast.LENGTH_LONG).show();
                innEndreTidspunkt.setHint("Velg en ny dato");
                innEndreTidspunkt.setEnabled(false);
            }
        };

        //Edittextviewet til tidspunkt sin onclick lytter som lager en ny TimePickerDialog boks:
        innEndreTidspunkt.setOnClickListener(view -> {
            int time = tidspunktKalender.get(Calendar.HOUR_OF_DAY);
            int minutt = tidspunktKalender.get(Calendar.MINUTE);
            TimePickerDialog dialog = new TimePickerDialog(EndreBestillingActivity.this, tidspunktDialogLytter, time, minutt, true);
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", dialog);
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Avslutt", dialog);
            dialog.show();
        });

    }

    //Her settes verdien i dato edittext:
    public void oppdaterDato(int year, int month, int day) {
        datoKalender.set(Calendar.YEAR, year);
        datoKalender.set(Calendar.MONTH, month);
        datoKalender.set(Calendar.DAY_OF_MONTH, day);
        String format = "dd-MM-yyyy";
        SimpleDateFormat datoFormat = new SimpleDateFormat(format, Locale.getDefault());

        innEndreDato.setText(datoFormat.format(datoKalender.getTime()));
        innEndreTidspunkt.setText("");
        innEndreTidspunkt.setEnabled(true);
    }

    public void endreBestilling(View view){
        if (!innEndreDato.getText().toString().isEmpty() && !innEndreTidspunkt.getText().toString().isEmpty()) {
            SparseBooleanArray checkedItemPositions = innEndreVenner.getCheckedItemPositions();
            venneListe.clear();
            //Finner alle vennene som har blitt krysset av i listen:
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
            Toast.makeText(this, "Fyll inn alle feltene!", Toast.LENGTH_SHORT).show();
        }
    }

    //Metode for å sette restauranten sitt navn inn i spinneren:
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

    //Metode for å legge venner som bestillingen har inn i listviewet:
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
            //Hvis noen av vennene i bestillingen har blitt slettet etter at bestillingen ble lagret:
            if (displayAlert) new AlertDialog.Builder(this)
                        .setTitle("Denne ordren inkluderer venner som har blitt slettet")
                        .setMessage("Hvis du fortsetter med å endre bestillingen, alle referanser til disse venner vil bli fjernet.")
                        .setPositiveButton(R.string.fortsett, null)
                        .setNegativeButton(R.string.avslutt, (dialog, which) -> onBackPressed())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
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
