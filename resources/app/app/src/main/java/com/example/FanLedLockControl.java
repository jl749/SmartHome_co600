package com.example;

import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;

public class FanLedLockControl {
    private static final String ip = "http://192.168.1.72/";

    public void run(String val, boolean on,String apiKey,String function) {
        SetFLC set = new SetFLC(val, on, apiKey,function);
        set.execute();
    }

    private class SetFLC extends AsyncTask<Void, Void, Void> {

        HttpURLConnection request = null;
        String val;
        String state;
        String apiKey;
        String function;

        public SetFLC(String val, boolean on,String apiKey,String function) {
            super();
            this.apiKey = apiKey;
            this.val = val;
            this.function = function;
            if (on) {
                state = "1";
            } else{
                state = "0";
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(ip + function + val + "/" + state + "/" +apiKey);
                HttpURLConnection request=(HttpURLConnection) url.openConnection();
                request.setConnectTimeout(2000);
                request.connect();


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
