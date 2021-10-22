package com.example.mappe2_s188886_s344046;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SMSService extends Service {
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        /*
        String dato = sharedPreferences.getString("dato", "");
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String currentDate = format.format(Calendar.getInstance().getTime());

        if(dato != null && dato.equals(currentDate)) {
        */
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String melding = sharedPreferences.getString("defaultSmsMessage", "");
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            //For API versions 23 or lower
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel("42") == null) {
                notificationManager.createNotificationChannel(new NotificationChannel("42", "SMSNotifChannel", NotificationManager.IMPORTANCE_DEFAULT));
            }

            Intent i = new Intent(this, FrontPageActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
            Notification notification = new NotificationCompat.Builder(this, "42")
                    .setContentTitle("Restaurant bestilling")
                    .setContentText(melding)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent).build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(0, notification);
        //}

        /*
        //Need to check if there is a restaurant booking today, if it is we send the default message that's stored in shared preferences:
        DBHandler db = new DBHandler(context);
        //Noe som det her men med bestillinger og ikke restauranter
        List<Restaurant> restaurantListe = db.finnAlleRestauranter();

        SmsManager sms = SmsManager.getDefault();
        SharedPreferences sharedPrefs =  context.getSharedPreferences("com.example.mappe2_s188886_s344046", Context.MODE_PRIVATE);
        String message = sharedPrefs.getString("smsMessage", "");
        for(int j = 0; j < restaurantListe.size() ; j++) {
            sms.sendTextMessage(restaurantListe.get(j).getTelefon(), null, message, null, null);
        }
         */

        return super.onStartCommand(intent, flags, startId);
    }
}
