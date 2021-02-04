package com.example;

import android.app.Activity;
import android.os.AsyncTask;

import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

public class LedControl extends Activity {

    public void run(String val, boolean on) {
        SetLED set = new SetLED(val, on);
        set.execute();
    }

    private class SetLED extends AsyncTask<Void, Void, Void> {

        private static final String ip = "http://192.168.1.251/";
        HttpsURLConnection https = null;
        String val;
        String state;

        public SetLED(String val, boolean on) {
            super();
            this.val = val;
            if (on) {
                state = "1";
            } else if (!on) {
                state = "0";
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(ip + "LED" + val + "/" + state);
                URLConnection con = url.openConnection();
                con.setConnectTimeout(1000);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
