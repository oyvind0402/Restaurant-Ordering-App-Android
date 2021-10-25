package com.example.mappe2_s188886_s344046.bestillinger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
        myToolbar.inflateMenu(R.menu.bestilling_menu);
        setSupportActionBar(myToolbar);

        TextView textView = (TextView) findViewById(R.id.bestillinger);
        DBHandler db = new DBHandler(this);
        StringBuilder ut = new StringBuilder();
        List<Bestilling> allebestillinger = db.finnALleBestillinger();
        for (Bestilling bestilling: allebestillinger) {
            ut.append("Restaurant: ")
                    .append(db.finnRestaurant(bestilling.getRestaurantid()).getNavn())
                    .append("\n")
                    .append("Dato: ")
                    .append(bestilling.getDato())
                    .append("\n").append("Tidpunkt: ")
                    .append(bestilling.getTidspunkt())
                    .append("\n")
                    .append("Venner: ");
            for(int i = 0; i < bestilling.getVenner().size(); i++) {
                if(bestilling.getVenner().size() > 0) {
                    ut.append(db.finnVenn(bestilling.getVenner().get(i).get_id()).getNavn()).append(" ");
                }
            }
            ut.append("\n\n");
        }
        textView.setText(ut.toString());
    }

    public void lagreBestilling(View view) {
        Intent intent = new Intent(this, LagreBestillingActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bestilling_menu, menu);
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
        } else if(item.getItemId() == R.id.friend_activity) {
            Intent i3 = new Intent(this, LagreVennActivity.class);
            startActivity(i3);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
