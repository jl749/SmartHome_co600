package com.example;

import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;

public class DismissAlarm {

    public void run(String apiKey){
        DismAlarm da = new DismAlarm(apiKey);
        da.execute();
    }

    private static class DismAlarm extends AsyncTask<Void, Void, Void> {

        HttpURLConnection request = null;
        URL url = null;
        String apiKey;

        private static final String ip = MainActivity.nodMCUwebServer;

        public DismAlarm(String apiKey) {
            super();
            this.apiKey = apiKey;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                url = new URL(ip + "/A_DISMISS/" + apiKey);
                request=(HttpURLConnection) url.openConnection();
                request.setConnectTimeout(5000);
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
