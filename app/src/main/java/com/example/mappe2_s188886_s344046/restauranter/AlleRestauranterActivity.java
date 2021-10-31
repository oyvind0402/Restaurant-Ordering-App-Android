package com.example.mappe2_s188886_s344046.restauranter;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mappe2_s188886_s344046.ForsideActivity;
import com.example.mappe2_s188886_s344046.R;
import com.example.mappe2_s188886_s344046.settings.SettingsActivity;
import com.example.mappe2_s188886_s344046.utils.DBHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlleRestauranterActivity extends AppCompatActivity {

    private DBHandler db;
    ListView listView;
    Restaurant restaurant;
    Button endreRestaurant, slettRestaurant;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allerestauranter_layout);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);

        endreRestaurant = (Button) findViewById(R.id.endre_restaurant_btn);
        slettRestaurant = (Button) findViewById(R.id.slett_restaurant_btn);

        db = new DBHandler(getApplicationContext());

        listView = (ListView) findViewById(R.id.restaurantListView);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        populateRestaurantList();
   }

   //Metode for å liste alle restaurantene i listviewet og for å lage lytteren til hvilken restaurant som er valg i listviewet:
   public void populateRestaurantList(){
       List<Restaurant> restaurantListe = db.finnAlleRestauranter();
       //Hvis det finnes noen restauranter:
       if(restaurantListe.size() > 0) {
           int blue = getResources().getColor(R.color.blue_logo);
           endreRestaurant.setEnabled(true);
           endreRestaurant.setBackgroundColor(blue);
           slettRestaurant.setEnabled(true);
           endreRestaurant.setBackgroundColor(blue);
           populateRestaurantListView(restaurantListe);
           listView.setOnItemClickListener((parent, view, i, id) -> {
               HashMap<String, String> hm = (HashMap<String, String>) listView.getItemAtPosition(i);
               String restaurantNavn = hm.get("item");
               restaurant = db.finnRestaurant(restaurantNavn) ;
           });
       //Ingen restauranter:
       } else {
           int grey = getResources().getColor(R.color.gray_logo);
           endreRestaurant.setEnabled(false);
           endreRestaurant.setBackgroundColor(grey);
           slettRestaurant.setEnabled(false);
           slettRestaurant.setBackgroundColor(grey);
           List<String> placeholderList = new ArrayList<>();
           placeholderList.add("Ingen restauranter lagt til enda!");
           ArrayAdapter<String> placeholderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, placeholderList);
           listView.setAdapter(placeholderAdapter);
       }
   }

   //Metode for å legge verdier i listen av restauranter og for å lage adapteren som listen skal settes i:
   public void populateRestaurantListView(List<Restaurant> list){
       List<Map<String, String>> data = new ArrayList<>();
       for (Restaurant r : list) {
           Map<String, String> datum = new HashMap<>(2);

           datum.put("item", r.getNavn());

           datum.put("subitem", r.getAdresse() + "\nTelefon: " + r.getTelefon() + "\nType: " + r.getType());

           data.add(datum);
       }

       SimpleAdapter adapter = new SimpleAdapter(this, data,
               R.layout.simple_list_item_2_single_choice,
               new String[] {"item", "subitem"},
               new int[] {R.id.text1,
                       R.id.text2});
       listView.setAdapter(adapter);
   }

   public void lagreRestaurant(View view){
        Intent intent = new Intent(this, LagreRestaurantActivity.class);
        startActivity(intent);
        finish();
   }

   public void slettRestaurant(View view){
        //Hvis noe er valgt gir vi brukeren mulighet til å si ja eller nei om de vil slette:
        if (restaurant != null) {
            new AlertDialog.Builder(this).setTitle("Sletting av " + restaurant.getNavn()).setMessage("Er du sikker på at du vil slette " + restaurant.getNavn() + "?").setPositiveButton("Ja", (dialogInterface, i) -> {
                db.slettRestaurant(restaurant.getId());
                Intent intent = new Intent(getApplicationContext(), AlleRestauranterActivity.class);
                startActivity(intent);
                finish();
            }).setNegativeButton("Nei", (dialogInterface, i) -> Toast.makeText(getApplicationContext(), "Sletting av " + restaurant.getNavn() + " avbrutt.", Toast.LENGTH_SHORT).show()).create().show();
        } else {
            Toast.makeText(this, "Velg en restaurant!", Toast.LENGTH_SHORT).show();
        }
   }

   public void endreRestaurant(View view){
        //Hvis noe er valgt legger vi restaurantId'en i et nøkkel-verdipar:
        if (restaurant != null) {
            Intent intent = new Intent(this, EndreRestaurantActivity.class);
            Bundle bundle = new Bundle();
            bundle.putLong("restaurantId", restaurant.getId());
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }  else {
            Toast.makeText(this, "Velg en restaurant!", Toast.LENGTH_SHORT).show();
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
}
