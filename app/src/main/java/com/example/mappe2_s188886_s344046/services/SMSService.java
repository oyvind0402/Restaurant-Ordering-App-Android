package com.example.mappe2_s188886_s344046.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.example.mappe2_s188886_s344046.ForsideActivity;
import com.example.mappe2_s188886_s344046.R;
import com.example.mappe2_s188886_s344046.bestillinger.AlleBestillingerActivity;
import com.example.mappe2_s188886_s344046.bestillinger.Bestilling;
import com.example.mappe2_s188886_s344046.utils.DBHandler;
import com.example.mappe2_s188886_s344046.venner.Venn;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SMSService extends Service {
    private String restaurantNavn;
    private String tidspunkt;
    private List<Venn> venner;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DBHandler db = new DBHandler(getApplicationContext());
        List<Bestilling> bestillingsListe = db.finnALleBestillinger();
        String dagensDato = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        if(orderIsToday(bestillingsListe, dagensDato, db)) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            StringBuilder venneString = new StringBuilder();
            if(venner.size() > 0) {
                for(Venn venn: venner) {
                    venneString.append(venn.getNavn()).append(", ");
                }
                venneString.delete(venneString.length()-2, venneString.length());
            }
            boolean notificationIsActivated = sharedPreferences.getBoolean("notifikasjon", false);
            //Sender notifikasjon hvis den ikke er skrudd av av brukeren i settings:
            if(notificationIsActivated) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                //For API versjoner 23 eller lavere
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel("42") == null) {
                    notificationManager.createNotificationChannel(new NotificationChannel("42", "SMSNotifChannel", NotificationManager.IMPORTANCE_DEFAULT));
                }

                Intent i = new Intent(this, ForsideActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
                Notification notification = new NotificationCompat.Builder(this, "42")
                        .setContentTitle("Restaurant bestilling")
                        .setContentText("Trykk her for å se bestillingen!")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText("Du har en restaurantbestilling i dag, trykk på notifikasjonen for å se dine bestillinger!"))
                        .setContentIntent(pendingIntent).build();
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                notificationManager.notify(0, notification);
            }

            boolean smsIsActivated = sharedPreferences.getBoolean("sms", false);
            //Sender sms hvis SMS er aktivert i settings og du har fått permissions av brukeren:
            if(smsIsActivated && ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                SmsManager sms = SmsManager.getDefault();
                String melding = sharedPreferences.getString("smsMelding", "Du har en bestilling i dag!");
                for(int j = 0; j < bestillingsListe.size() ; j++) {
                    if(bestillingsListe.get(j).getDato().equals(dagensDato)) {
                        if(bestillingsListe.get(j).getVenner().size() > 0) {
                            for(int i = 0; i < bestillingsListe.get(j).getVenner().size(); i++) {
                                sms.sendTextMessage(bestillingsListe.get(j).getVenner().get(i).getTelefon(), null, melding, null, null);
                            }
                        }
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public boolean orderIsToday(List<Bestilling> bestillingsListe, String dato, DBHandler db) {
        for (Bestilling bestilling : bestillingsListe) {
            if (bestilling.getDato().equals(dato)) {
                try {
                    restaurantNavn = db.finnRestaurant(bestilling.getRestaurantid()).getNavn();
                    tidspunkt = bestilling.getTidspunkt();
                    venner = bestilling.getVenner();
                    return true;

                } catch (NullPointerException e) {
                    return false;
                }
            }
        }
        return false;
    }
}
