package com.example.mappe2_s188886_s344046.bestillinger;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mappe2_s188886_s344046.R;
import com.example.mappe2_s188886_s344046.restauranter.Restaurant;
import com.example.mappe2_s188886_s344046.utils.DBHandler;
import com.example.mappe2_s188886_s344046.venner.Venn;

import java.util.ArrayList;
import java.util.List;

public class EndreBestillingActivity extends AppCompatActivity {
    DBHandler db;
    long bestillingId;
    Spinner innEndreRestaurant;
    ListView innEndreVenner;
    EditText innEndreDato, innEndreTidspunkt;
    List<Venn> venneListe;
    Bestilling bestilling;

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

        Bundle bundle = getIntent().getExtras();
        bestillingId = bundle.getLong("bestillingId");
        bestilling = db.finnBestilling(bestillingId);
        lasteVenner();
        lasteRestaurant();
        innEndreDato.setText(bestilling.getDato());
        innEndreTidspunkt.setText(bestilling.getTidspunkt());
    }

    public void endreBestilling(){

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
                    View curVenn = vennAdapter.getView(i, null, innEndreVenner);
                    for (Venn venn : venneListe) {
                        if (curVenn.getTransitionName().equals(venn.toString())) {
                            Log.w("ANA", "TRUE");
                            //TODO Make this work
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
}
