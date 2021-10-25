package com.example.mappe2_s188886_s344046.venner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.example.mappe2_s188886_s344046.bestillinger.AlleBestillingerActivity;
import com.example.mappe2_s188886_s344046.utils.DBHandler;
import com.example.mappe2_s188886_s344046.ForsideActivity;
import com.example.mappe2_s188886_s344046.restauranter.LagreRestaurantActivity;
import com.example.mappe2_s188886_s344046.R;

import java.util.List;

public class AlleVennerActivity extends AppCompatActivity {
    private DBHandler db;
    ListView listView;
    Venn venn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allevenner_layout);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.inflateMenu(R.menu.venn_menu);
        setSupportActionBar(myToolbar);

        db = new DBHandler(getApplicationContext());
        listView = (ListView) findViewById(R.id.venneListView);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                venn = (Venn) listView.getItemAtPosition(i);
                Log.d("TAG", "Venn: " + venn);
            }
        });
        populateFriendList();
    }

    public void populateFriendList() {
        List<Venn> venneListe = db.finnAlleVenner();
        ArrayAdapter<Venn> vennAdapter = new ArrayAdapter<Venn>(this, android.R.layout.simple_list_item_single_choice, venneListe);
        listView.setAdapter(vennAdapter);
    }

    public void lagreVenn(View view) {
        Intent intent = new Intent(this, LagreVennActivity.class);
        startActivity(intent);
        finish();
    }

    public void slettVenn(View view) {
        if(venn != null) {
            db.slettVenn(venn.getId());
            recreate();
        } else {
            Toast.makeText(this, "Du må velge en venn for å slette!", Toast.LENGTH_SHORT).show();
        }
    }

    public void endreVenn(View view) {
        if(venn != null) {
            Intent intent = new Intent(this, EndreVennActivity.class);
            Bundle bundle = new Bundle();
            bundle.putLong("vennId", venn.getId());
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Du må velge en venn for å oppdatere!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.venn_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.frontpage_activity) {
            Intent i = new Intent(this, ForsideActivity.class);
            startActivity(i);
        } else if(item.getItemId() == R.id.restaurant_activity) {
            Intent i2 = new Intent(this, LagreRestaurantActivity.class);
            startActivity(i2);
        } else if(item.getItemId() == R.id.order_activity) {
            Intent i3 = new Intent(this, AlleBestillingerActivity.class);
            startActivity(i3);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
