package com.example.mappe2_s188886_s344046.bestillinger;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mappe2_s188886_s344046.restauranter.AlleRestauranterActivity;
import com.example.mappe2_s188886_s344046.settings.SettingsActivity;
import com.example.mappe2_s188886_s344046.utils.DBHandler;
import com.example.mappe2_s188886_s344046.ForsideActivity;
import com.example.mappe2_s188886_s344046.R;
import com.example.mappe2_s188886_s344046.restauranter.LagreRestaurantActivity;
import com.example.mappe2_s188886_s344046.venner.LagreVennActivity;

import java.util.List;

public class AlleBestillingerActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allebestillinger_layout);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.inflateMenu(R.menu.menu);
        setSupportActionBar(myToolbar);

        LinearLayout container = (LinearLayout) findViewById(R.id.bestillinger);
        DBHandler db = new DBHandler(this);
        List<Bestilling> allebestillinger = db.finnALleBestillinger();
        for (Bestilling bestilling: allebestillinger) {
            TextView textView = new TextView(this);
            StringBuilder ut = new StringBuilder();
            try {
            ut.append("Restaurant: " + db.finnRestaurant(bestilling.getRestaurantid()).getNavn())
                    .append("\nDato: ")
                    .append(bestilling.getDato())
                    .append("\nTidpunkt: ")
                    .append(bestilling.getTidspunkt());
                if (bestilling.getVenner().size() > 0) {
                    ut.append("\nVenner: ");
                    for (int i = 0; i < bestilling.getVenner().size(); i++) {
                        try {
                            ut.append(db.finnVenn(bestilling.getVenner().get(i).getId()).getNavn());
                        } catch (NullPointerException e) {
                            ut.append("Venn har blitt slettet");
                        }
                        if (i < bestilling.getVenner().size() - 1) {
                            ut.append("; ");
                        }
                    }
                }
                textView.setOnClickListener((View view) -> {
                    Intent intent = new Intent(getApplicationContext(), EndreBestillingActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putLong("bestillingId", bestilling.getId());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                });
            } catch (NullPointerException e) {
                if (db.finnRestaurant(bestilling.getRestaurantid()) == null) {
                    ut.append("Bestilling #").append(bestilling.getId()).append(" er ugyldig. \n");
                }
                textView.setOnClickListener((View view) -> {
//                    Toast.makeText(this, "Restauranten har blitt slettet", Toast.LENGTH_SHORT).show();
                //TODO Move this out
                    db.slettBestilling(bestilling.getId());
                    Intent intent = new Intent(this, AlleBestillingerActivity.class);
                    startActivity(intent);
                    finish();
                });
            }

            ut.append("\n");
            textView.setText(ut.toString());
            container.addView(textView);
        }
    }

    public void lagreBestilling(View view) {
        Intent intent = new Intent(this, LagreBestillingActivity.class);
        startActivity(intent);
        finish();
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
