package com.example.mappe2_s188886_s344046.restauranter;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mappe2_s188886_s344046.ForsideActivity;
import com.example.mappe2_s188886_s344046.R;
import com.example.mappe2_s188886_s344046.bestillinger.AlleBestillingerActivity;
import com.example.mappe2_s188886_s344046.settings.SettingsActivity;
import com.example.mappe2_s188886_s344046.utils.DBHandler;
import com.example.mappe2_s188886_s344046.venner.LagreVennActivity;

import java.util.List;

public class AlleRestauranterActivity extends AppCompatActivity {

    private DBHandler db;
    ListView listView;
    Restaurant restaurant;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allerestauranter_layout);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.inflateMenu(R.menu.menu);
        setSupportActionBar(myToolbar);

        db = new DBHandler(getApplicationContext());

        listView = (ListView) findViewById(R.id.restaurantListView);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                restaurant = (Restaurant) listView.getItemAtPosition(i);
            }
        });

        populateRestaurantList();
   }

   public void populateRestaurantList(){
       List<Restaurant> restaurantListe = db.finnAlleRestauranter();
       ArrayAdapter<Restaurant> restaurantAdapter = new ArrayAdapter<>(this, R.layout.restaurant_listview_layout, restaurantListe);
       listView.setAdapter(restaurantAdapter);
   }

   public void lagreRestaurant(View view){
        Intent intent = new Intent(this, LagreRestaurantActivity.class);
        startActivity(intent);
        finish();
   }

   public void slettRestaurant(View view){
        if (restaurant != null) {
            db.slettRestaurant(restaurant.getId());
            Intent intent = new Intent(this, AlleRestauranterActivity.class);
            startActivity(intent);
            finish();
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
