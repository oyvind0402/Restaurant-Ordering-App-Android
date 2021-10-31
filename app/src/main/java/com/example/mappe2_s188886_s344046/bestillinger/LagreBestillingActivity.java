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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mappe2_s188886_s344046.ForsideActivity;
import com.example.mappe2_s188886_s344046.R;
import com.example.mappe2_s188886_s344046.restauranter.LagreRestaurantActivity;
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

        //Lager to kalendere for tidspunkt og dato:
        tidspunktKalender = Calendar.getInstance();
        datoKalender = Calendar.getInstance();

        //Setter lytteren for når en dato blir valgt i DatePickerDialog boksen:
        datoDialogLytter = (view, year, month, day) -> oppdaterDato(year, month, day);

        //Setter onclick lytter for edittextviewet for dato, den lager en ny DatePickerDialog boks:
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

    //Her settes verdien i dato edittext:
    public void oppdaterDato(int year, int month, int day) {
        datoKalender.set(Calendar.YEAR, year);
        datoKalender.set(Calendar.MONTH, month);
        datoKalender.set(Calendar.DAY_OF_MONTH, day);
        String format = "dd-MM-yyyy";
        SimpleDateFormat datoFormat = new SimpleDateFormat(format, Locale.getDefault());

        innDato.setText(datoFormat.format(datoKalender.getTime()));
        aktivereTid(year, month, day);
    }

    //Metode for å sette lytteren til edittextviewet til tidspunkt, og for OnTimeSet lytteren til TimePickerDialog boksen som startes når man trykker på edittextviewet:
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

    //Metode for å sette verdier til spinneren - spesifikt restaurant navn -
    // den setter også en lytter for spinneren sin OnItemSelected som lager en ny restaurant ut ifra det navnet som er valgt:
    public void populateSpinner() {
        List<Restaurant> restaurantListe = db.finnAlleRestauranter();

        // Valgte en Map + Liste løsning, slik at nøkkel-verdiene er alltid paret (ingen fare for unsync)
        if(restaurantListe.size() > 0) {
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
                        //Hvis det ikke er placeholder verdien som er valgt så initialiserer vi noen variabler
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
        } else {
            List<String> placeholderListe = new ArrayList<>();
            placeholderListe.add("Ingen restauranter");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(LagreBestillingActivity.this, android.R.layout.simple_spinner_item, placeholderListe);
            spinner.setAdapter(adapter);
            spinner.setEnabled(false);
        }

    }

    //Metode for å legge venner inn i listviewet for venner:
    public void populateFriendList() {
        List<Venn> venneListe2 = db.finnAlleVenner();
        if (venneListe2.size() > 0) {
            ArrayAdapter<Venn> vennAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, venneListe2);
            listView.setAdapter(vennAdapter);
        } else {
            List<String> placeholderListe = new ArrayList<>();
            placeholderListe.add("Ingen venner");
            ArrayAdapter<String> placeholderAdapter = new ArrayAdapter<>(LagreBestillingActivity.this, android.R.layout.simple_list_item_1, placeholderListe);
            listView.setAdapter(placeholderAdapter);
            listView.setEnabled(false);
        }
    }

    public void lagreBestilling(View view) {
        SparseBooleanArray checkedItemPositions = listView.getCheckedItemPositions();
        for (int i = 0; i < checkedItemPositions.size(); i++) {
            if(checkedItemPositions.valueAt(i)) {
                Venn venn = (Venn) listView.getItemAtPosition(i);
                venneListe.add(venn);
            }
        }

        try {
            if (!innDato.getText().toString().isEmpty() && !innTidspunkt.getText().toString().isEmpty() && spinner.getSelectedItemPosition() != liste.size() - 1) {
                Bestilling bestilling = new Bestilling(restaurantid, innDato.getText().toString(), innTidspunkt.getText().toString(), venneListe);
                db.leggTilBestilling(bestilling);
                populateSpinner();
                populateFriendList();
                innDato.setText("");
                innTidspunkt.setText("");

                Toast.makeText(this, "Bestilling av bord hos " + db.finnRestaurant(restaurantid).getNavn() + " bekreftet.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Fyll inn alle feltene!", Toast.LENGTH_LONG).show();
            }
            venneListe.clear();
        //Hvis det er en tom restaurantliste gir vi brukeren mulighet til å gå rett til lagre restaurant ved bruk av en AlertDialog boks.
        //Sender med en et verdipar slik at onBackPressed metoden i LagreRestaurant vil gå tilbake til riktig aktivitet hvis vi kom fra LagreBestilling:
        } catch (NullPointerException e) {
            new AlertDialog.Builder(this).setTitle("Tom restaurantliste").setMessage("Vil du lagre en ny restaurant?").setPositiveButton("Ja", (dialogInterface, i) -> {
                Intent intent = new Intent(getApplicationContext(), LagreRestaurantActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("tilbaketil", "LagreBestilling");
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }).setNegativeButton("Nei", null).show();

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
