package com.example;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AlarmReceiver extends BroadcastReceiver {

    final Handler handler = new Handler(Looper.getMainLooper());
    private static final String CHANNEL_ID = "intruder";
    private String houseID;


    @Override
    public void onReceive(final Context context, Intent intent) {
        houseID = intent.getStringExtra("HouseID");
        UpdateValues uv = new UpdateValues();
        uv.run(((MyApplication)context.getApplicationContext()));
        GetAlarmAndTemp gat = new GetAlarmAndTemp();
        gat.run(((MyApplication)context.getApplicationContext()),houseID);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkIntruder(context);
            }
        }, 3000);//increase time
    }

    public void checkIntruder(Context c){
        boolean intruder = ((MyApplication)c.getApplicationContext()).getIntruder();
        String alarm = ((MyApplication)c.getApplicationContext()).getAlarm();

        Intent dial = new Intent(Intent.ACTION_DIAL);
        dial.setData(Uri.parse("tel:999"));
        dial.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(c, 0, dial, 0);

        PendingIntent dismissIntent = CloseNotification.getDismissIntent(1, c);

        if (intruder && alarm.equals("1")){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(c, CHANNEL_ID)
                    .setSmallIcon(R.drawable.mainicon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentTitle("Intrusion Alarm.")
                    .setContentText("Intruder has been detected in house: "+houseID)
                    .setAutoCancel(true)
                    .addAction(0, "Call Police", intent)
                    .addAction(0, "Dismiss", dismissIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(NotificationCompat.CATEGORY_ALARM);
            System.out.println("INTRUDER ALERT!");
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(c);
            notificationManager.notify(1, builder.build());
            }
    }
}
