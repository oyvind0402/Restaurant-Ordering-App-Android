package com.example.mappe2_s188886_s344046.venner;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.example.mappe2_s188886_s344046.R;
import com.example.mappe2_s188886_s344046.settings.SettingsActivity;
import com.example.mappe2_s188886_s344046.utils.DBHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlleVennerActivity extends AppCompatActivity {
    private DBHandler db;
    ListView listView;
    Venn venn;
    Button endreVenn, slettVenn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allevenner_layout);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.inflateMenu(R.menu.menu);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);

        endreVenn = (Button) findViewById(R.id.endre_venn_btn);
        slettVenn = (Button) findViewById(R.id.slett_venn_btn);

        db = new DBHandler(getApplicationContext());
        listView = (ListView) findViewById(R.id.venneListView);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        populateFriendList();
    }

    public void populateFriendList() {
        List<Venn> venneListe = db.finnAlleVenner();
        if (venneListe.size() > 0) {
            endreVenn.setEnabled(true);
            slettVenn.setEnabled(true);
            populateVennListView(venneListe);
            listView.setOnItemClickListener((adapterView, view, i, l) -> {
                HashMap<String, String> hm = (HashMap<String, String>) listView.getItemAtPosition(i);
                String venneNavn = hm.get("item");
            });
        } else {
             endreVenn.setEnabled(false);
             slettVenn.setEnabled(false);
             List<String> placeholderList = new ArrayList<>();
             placeholderList.add("Ingen venner lagt til enda!");
             ArrayAdapter<String> placeholderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, placeholderList);
             listView.setAdapter(placeholderAdapter);
        }
    }

    public void populateVennListView(List<Venn> list) {
        List<Map<String, String>> data = new ArrayList<>();
        for(Venn venn : list) {
            Map<String, String> datum = new HashMap<>(2);
            datum.put("item", venn.getNavn());
            datum.put("subitem", "Telefon: " + venn.getTelefon());

            data.add(datum);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, data,
                R.layout.simple_list_item_2_single_choice,
                new String[]{"item", "subitem"},
                new int[] {R.id.text1, R.id.text2});
        listView.setAdapter(adapter);
    }

    public void lagreVenn(View view) {
        Intent intent = new Intent(this, LagreVennActivity.class);
        startActivity(intent);
        finish();
    }

    public void slettVenn(View view) {
        if(venn != null) {
            new AlertDialog.Builder(this).setTitle("Sletting av " + venn.getNavn()).setMessage("Er du sikker på at du vil slette " + venn.getNavn() + "?").setPositiveButton("Ja", (dialogInterface, i) -> {
                db.slettVenn(venn.getId());
                Intent intent = new Intent(getApplicationContext(), AlleVennerActivity.class);
                startActivity(intent);
                finish();
            }).setNegativeButton("Nei", (dialogInterface, i) -> Toast.makeText(getApplicationContext(), "Sletting av " + venn.getNavn() + " avbrutt.", Toast.LENGTH_SHORT).show()).create().show();
        } else {
            Toast.makeText(this, "Velg en venn!", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Velg en venn!", Toast.LENGTH_SHORT).show();
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
