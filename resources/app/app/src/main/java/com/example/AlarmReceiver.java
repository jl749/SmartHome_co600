package com.example;


import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

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
    
    private static final String CHANNEL_ID = "intruder";
    private String houseID;

    @Override
    public void onReceive(Context context, Intent intent) {
        GetHttp h = new GetHttp(context);
        houseID = intent.getStringExtra("HouseID");
        h.execute();
    }


    public void checkIntruder(ArrayList<String> array,Context c){
        if(!array.isEmpty()) {

            String alarm = array.get(7).replace("\"", "");

            Intent dial = new Intent(Intent.ACTION_DIAL);
            dial.setData(Uri.parse("tel:999"));
            dial.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent = PendingIntent.getActivity(c, 0, dial, 0);

            PendingIntent dismissIntent = CloseNotification.getDismissIntent(1, c);

            if (alarm.equals("1")) {
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


    private class GetHttp extends AsyncTask<Void, Void, Void> {
        ArrayList<String> variables = new ArrayList<String>();
        String result;
        Context c;

        public GetHttp(Context c){
            this.c = c;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url;
                url = new URL("https://raptor.kent.ac.uk/~jl749/status.html");
                URLConnection con = url.openConnection();
                con.setConnectTimeout(1000);
                con.setReadTimeout(1000);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                try {
                    String stringBuffer;
                    String string = "";
                    while ((stringBuffer = bufferedReader.readLine()) != null) {
                        string = String.format("%s%s", string, stringBuffer);
                    }
                    result = string;
                } finally {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                result = e.toString();
            }


            Pattern p = Pattern.compile("\"([^\"]*)\"");
            Matcher m = p.matcher(result);
            while (m.find()) variables.add(m.group());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            checkIntruder(variables,c);
        }
    }

}
