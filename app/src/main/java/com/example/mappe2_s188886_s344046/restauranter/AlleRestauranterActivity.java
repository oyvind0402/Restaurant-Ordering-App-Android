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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mappe2_s188886_s344046.R;
import com.example.mappe2_s188886_s344046.settings.SettingsActivity;
import com.example.mappe2_s188886_s344046.utils.DBHandler;
import com.example.mappe2_s188886_s344046.venner.AlleVennerActivity;

import java.util.ArrayList;
import java.util.List;

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
        myToolbar.inflateMenu(R.menu.menu);
        setSupportActionBar(myToolbar);

        endreRestaurant = (Button) findViewById(R.id.endre_restaurant_btn);
        slettRestaurant = (Button) findViewById(R.id.slett_restaurant_btn);

        db = new DBHandler(getApplicationContext());

        listView = (ListView) findViewById(R.id.restaurantListView);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        populateRestaurantList();
   }

   public void populateRestaurantList(){
       List<Restaurant> restaurantListe = db.finnAlleRestauranter();
       if(restaurantListe.size() > 0) {
           endreRestaurant.setEnabled(true);
           slettRestaurant.setEnabled(true);
           ArrayAdapter<Restaurant> restaurantAdapter = new ArrayAdapter<>(this, R.layout.restaurant_listview_layout, restaurantListe);
           listView.setAdapter(restaurantAdapter);
           listView.setOnItemClickListener((parent, view, i, id) -> restaurant = (Restaurant) listView.getItemAtPosition(i));
       } else {
           endreRestaurant.setEnabled(false);
           slettRestaurant.setEnabled(false);
           List<String> placeholderList = new ArrayList<>();
           placeholderList.add("Ingen restauranter lagt til enda!");
           ArrayAdapter<String> placeholderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, placeholderList);
           listView.setAdapter(placeholderAdapter);
       }
   }

   public void lagreRestaurant(View view){
        Intent intent = new Intent(this, LagreRestaurantActivity.class);
        startActivity(intent);
        finish();
   }

   public void slettRestaurant(View view){
        if (restaurant != null) {
            new AlertDialog.Builder(this).setTitle("Sletting av " + restaurant.getNavn()).setMessage("Er du sikker på at du vil slette " + restaurant.getNavn() + "?").setPositiveButton("Ja", (dialogInterface, i) -> {
                db.slettRestaurant(restaurant.getId());
                Intent intent = new Intent(getApplicationContext(), AlleRestauranterActivity.class);
                startActivity(intent);
                finish();
            }).setNegativeButton("Nei", (dialogInterface, i) -> Toast.makeText(getApplicationContext(), "Sletting av " + restaurant.getNavn() + " ikke vellykket.", Toast.LENGTH_SHORT).show()).create().show();
        } else {
            Toast.makeText(this, "Du må velge en restaurant for å slette", Toast.LENGTH_SHORT).show();
        }
   }

   public void endreRestaurant(View view){
        if (restaurant != null) {
            Intent intent = new Intent(this, EndreRestaurantActivity.class);
            Bundle bundle = new Bundle();
            bundle.putLong("restaurantId", restaurant.getId());
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }  else {
            Toast.makeText(this, "Du må velge en restaurant for å endre", Toast.LENGTH_SHORT).show();
        }
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
