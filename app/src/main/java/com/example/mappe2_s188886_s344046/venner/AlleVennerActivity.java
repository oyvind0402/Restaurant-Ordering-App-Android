package com.example.mappe2_s188886_s344046.venner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mappe2_s188886_s344046.R;
import com.example.mappe2_s188886_s344046.settings.SettingsActivity;
import com.example.mappe2_s188886_s344046.utils.DBHandler;

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
        myToolbar.inflateMenu(R.menu.menu);
        setSupportActionBar(myToolbar);

        db = new DBHandler(getApplicationContext());
        listView = (ListView) findViewById(R.id.venneListView);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            venn = (Venn) listView.getItemAtPosition(i);
            Log.d("TAG", "Venn: " + venn);
        });
        populateFriendList();
    }

    public void populateFriendList() {
        List<Venn> venneListe = db.finnAlleVenner();
        ArrayAdapter<Venn> vennAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, venneListe);
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
            Intent intent = new Intent(this, AlleVennerActivity.class);
            startActivity(intent);
            finish();
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
