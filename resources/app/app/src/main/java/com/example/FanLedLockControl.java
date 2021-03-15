package com.example;

import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;

public class FanLedLockControl {
    //private static final String ip = "http://192.168.1.72/";
    private static final String ip = MainActivity.nodMCUwebServer;

    public void run(String val, boolean on,String apiKey,String function) {
        SetFLC set = new SetFLC(val, on, apiKey,function);
        set.execute();
    }

    private class SetFLC extends AsyncTask<Void, Void, Void> {

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

            URL url = null;
            HttpURLConnection request = null;

            try {
                url = new URL(ip + function + val + "/" + state + "/" +apiKey);
                request=(HttpURLConnection) url.openConnection();
                request.connect();
                request.getInputStream().close();
                request.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
