package com.example;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.net.HttpURLConnection;
import java.net.URL;

public class CloseNotification extends AppCompatActivity {

    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
    public static  String apiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(getIntent().getIntExtra(NOTIFICATION_ID, -1));
        apiKey = ((MyApplication)this.getApplication()).getAPIKey();
        DismissAlarm da = new DismissAlarm(apiKey);
        da.execute();
        finish(); // since finish() is called in onCreate(), onDestroy() will be called immediately
    }

    public static PendingIntent getDismissIntent(int notificationId, Context context) {
        Intent intent = new Intent(context, CloseNotification.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(NOTIFICATION_ID, notificationId);
        PendingIntent dismissIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return dismissIntent;
    }

    private class DismissAlarm extends AsyncTask<Void, Void, Void> {

        HttpURLConnection request = null;
        String val;
        String state;
        String apiKey;
        String function;
        private static final String ip = MainActivity.nodMCUwebServer;

        public DismissAlarm(String apiKey) {
            super();
            this.apiKey = apiKey;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            URL url = null;
            try {
                url = new URL(ip + "/A_DISMISS/" + apiKey);
                request=(HttpURLConnection) url.openConnection();
                request.connect();
                request.getInputStream().close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                try{request.disconnect();}catch(Exception e) {e.printStackTrace();}
            }
            return null;
        }
    }
}
