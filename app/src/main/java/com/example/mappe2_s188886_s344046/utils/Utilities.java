package com.example.mappe2_s188886_s344046.utils;

import android.content.Context;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.mappe2_s188886_s344046.R;
import com.example.mappe2_s188886_s344046.bestillinger.Bestilling;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utilities {
    public static void formatDate(Calendar cal, int year, int month, int day, Integer hour, Integer min){
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        if (hour != null && min != null) {
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, min);
        }
    }

    //  Basert på: https://stackoverflow.com/questions/7916834/adding-listview-sub-item-text-in-android
    public static void populateListView(Context context, DBHandler db, ListView lv, List<Bestilling> list, int layout){
        List<Map<String, String>> data = new ArrayList<>();
        for (Bestilling bestilling : list) {
            Map<String, String> datum = new HashMap<>(2);

            String itemTxt = "";
            StringBuilder subitemTxt = new StringBuilder();
            try {
                String restaurantNavn = db.finnRestaurant(bestilling.getRestaurantid()).getNavn();
                itemTxt = String.format("Bestilling #%d hos %s", bestilling.getId(), restaurantNavn);
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
                itemTxt = String.format("Bestilling #%d er ugyldig.", bestilling.getId());
                if (db.finnRestaurant(bestilling.getRestaurantid()).getNavn() == null) {
                    subitemTxt.append("Den valgte restauranten har blitt slettet");
                }
            }

            datum.put("item", itemTxt);
            datum.put("subitem", subitemTxt.toString());
            data.add(datum);
        }

        SimpleAdapter adapter = new SimpleAdapter(context, data,
                layout,
                new String[] {"item", "subitem"},
                new int[] {R.id.text1,
                        R.id.text2});
        lv.setAdapter(adapter);
    }
}
