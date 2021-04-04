package com.example;

import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;

/*Class that sends http request to arduino to control the fan,LED,lock and heating pad*/
public class FanLedLockControl {

    private static final String ip = MainActivity.nodMCUwebServer;

    public void run(String val, boolean on,String apiKey,String function) {
        SetFLC set = new SetFLC(val, on, apiKey, function);
        set.execute();
    }

    private static class SetFLC extends AsyncTask<Void, Void, Void> {

        URL url = null;
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
                url = new URL(ip + function + val + "/" + state + "/" +apiKey);
                request=(HttpURLConnection) url.openConnection();
                request.setConnectTimeout(5000);
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
