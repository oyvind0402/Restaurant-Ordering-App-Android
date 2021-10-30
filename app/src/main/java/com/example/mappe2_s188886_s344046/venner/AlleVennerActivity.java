package com.example.mappe2_s188886_s344046.venner;

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

import com.example.mappe2_s188886_s344046.ForsideActivity;
import com.example.mappe2_s188886_s344046.R;
import com.example.mappe2_s188886_s344046.settings.SettingsActivity;
import com.example.mappe2_s188886_s344046.utils.DBHandler;

import java.util.ArrayList;
import java.util.List;

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
            ArrayAdapter<Venn> vennAdapter = new ArrayAdapter<>(this, R.layout.allevenner_listview_layout, venneListe);
            listView.setAdapter(vennAdapter);
            listView.setOnItemClickListener((adapterView, view, i, l) -> {
                venn = (Venn) listView.getItemAtPosition(i);
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
