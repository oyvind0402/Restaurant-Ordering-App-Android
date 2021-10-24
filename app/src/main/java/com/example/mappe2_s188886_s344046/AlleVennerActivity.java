package com.example.mappe2_s188886_s344046;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AlleVennerActivity extends AppCompatActivity {
    private DBHandler db;
    ListView listView;
    SimpleCursorAdapter simpleCursorAdapter;
    Cursor cursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allevenner_layout);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.inflateMenu(R.menu.venn_menu);
        setSupportActionBar(myToolbar);

        db = new DBHandler(getApplicationContext());
        populateFriendList();
    }

    public void populateFriendList() {
        cursor = db.finnVenner();
        listView = (ListView) findViewById(R.id.venneListView);
        int[] dataFields = new int[] {
                R.id.venn_navn,
                R.id.venn_telefon
        };
        String[] dataFieldValues = new String[] {
                "navn",
                "telefon"
        };
        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.venner_listview_layout, cursor, dataFieldValues, dataFields);
        listView.setAdapter(simpleCursorAdapter);
    }

    public void lagreVenn(View view) {
        Intent intent = new Intent(this, LagreVennActivity.class);
        startActivity(intent);
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
