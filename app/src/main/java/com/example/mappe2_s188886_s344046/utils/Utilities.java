package com.example.mappe2_s188886_s344046.utils;

import android.content.Context;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.mappe2_s188886_s344046.R;
import com.example.mappe2_s188886_s344046.bestillinger.Bestilling;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {
    //Metode for å formatere en dato:
    public static void formatDate(Calendar cal, int year, int month, int day, Integer hour, Integer min){
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        if (hour != null && min != null) {
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, min);
        }
    }

    //Metode for å legge inaktive bestillinger i en liste, aktive bestillinger i en annen liste:
    public static void populateBestillingList(DBHandler db, List<Bestilling> allebestillinger, List<Bestilling> aktiveBestillingerList, List<Bestilling> inaktiveBestillingerList){
        for (Bestilling bestilling: allebestillinger) {
            try {
                Calendar dagensDato = Calendar.getInstance(Locale.getDefault());
                Calendar valgtDato = Calendar.getInstance(Locale.getDefault());
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
                valgtDato.setTime(Objects.requireNonNull(df.parse(bestilling.getDato() + " " + bestilling.getTidspunkt())));

                //Hvis bestillingens dato er før dagens dato er den inaktiv:
                if (valgtDato.before(dagensDato)){
                    inaktiveBestillingerList.add(bestilling);
                } else {
                    try {
                        String restaurantNavn = db.finnRestaurant(bestilling.getRestaurantid()).getNavn();
                        if (aktiveBestillingerList != null) aktiveBestillingerList.add(bestilling);
                    //Hvis restauranten ikke finnes lenger (har blitt slettet) skal vi legge bestillingen i inaktive bestillinger:
                    }  catch (NullPointerException e) {
                        inaktiveBestillingerList.add(bestilling);
                    }

                }
            } catch (ParseException e) {
                Log.w("BESTILLING_ERR", "Kunne ikke parse en bestilling");
            }
        }
    }

    //Metode for å legge verdier fra en bestilling i en adapter og sette den i et listview:
    //  Basert på: https://stackoverflow.com/questions/7916834/adding-listview-sub-item-text-in-android
    public static void populateBestillingListView(Context context, DBHandler db, ListView lv, List<Bestilling> list, int layout){
        List<Map<String, String>> data = new ArrayList<>();
        //For hver bestilling skal vi legge bestillingsnummer og restaurant navn i item delen av et hashmap
        //Vi skal også legge vennene i en bestilling som subitem i et hashmap:
        for (Bestilling bestilling : list) {
            Map<String, String> datum = new HashMap<>(2);

            String itemTxt = "";
            StringBuilder subitemTxt = new StringBuilder();
            try {
                String restaurantNavn = db.finnRestaurant(bestilling.getRestaurantid()).getNavn();
                itemTxt = String.format(Locale.getDefault(), "Bestilling #%d hos %s", bestilling.getId(), restaurantNavn);
                subitemTxt = new StringBuilder(String.format("%s %s", bestilling.getDato(), bestilling.getTidspunkt()));

                int antallVenner = (bestilling).getVenner().size();
                if (antallVenner > 0) {
                    subitemTxt.append("\nVenner: ");
                    for (int i = 0; i < bestilling.getVenner().size(); i++) {
                        try {
                            subitemTxt.append(db.finnVenn(bestilling.getVenner().get(i).getId()).getNavn());
                        } catch (NullPointerException e) {
                            subitemTxt.append("[slettet]");
                        }
                        if (i < antallVenner - 1) {
                            subitemTxt.append("; ");
                        } else {
                            subitemTxt.append("\n");
                        }
                    }
                }
            } catch (NullPointerException e){
                itemTxt = String.format(Locale.getDefault(), "Bestilling #%d er ugyldig.", bestilling.getId());
                subitemTxt.append("Den valgte restauranten har blitt slettet");
            }

            datum.put("item", itemTxt);
            datum.put("subitem", subitemTxt.toString());
            //Hashmap'et blir lagt inn i listen av hashmap som skal brukes til adapteren for listviewet, for å kunne ha to items i et listview under hverandre:
            data.add(datum);
        }

        SimpleAdapter adapter = new SimpleAdapter(context, data,
                layout,
                new String[] {"item", "subitem"},
                new int[] {R.id.text1,
                        R.id.text2});
        lv.setAdapter(adapter);
    }

    //Metode for å ta ut ID'en fra "item" delen av et hashmap:
    public static long extractId(String s) throws Exception {
        Pattern p = Pattern.compile("#[0-9]+");
        Matcher matcher = p.matcher(s);

        if (matcher.find())
        {
            String found = matcher.group(0);
            try {
                found = found.substring(1);
                return Long.parseLong(found);
            } catch (Exception e){
                throw new Exception(e.getMessage());
            }
        }
        throw new Exception("Ingen match.");
    }
}
