package com.example;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

public class SetTemperature extends Activity {

    public void run(Double val, int houseID){
        setTemp set = new setTemp(val,houseID);
        set.execute();
    }

    public class setTemp extends AsyncTask<Void, Void, Void> {

        HttpsURLConnection https = null;
        OutputStream out = null;
        InputStreamReader in = null;
        BufferedReader reader = null;
        private static final String raptor = "https://raptor.kent.ac.uk/~jl749/";
        Double val;
        int houseID;


        public setTemp(Double val, int houseID) {
            super();
            this.val = val;
            this.houseID = houseID;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(raptor + "update_tmp_intruder.php");
                String msg = "tmp=" + val + "&houseID=" + houseID;
                byte[] postDataBytes = msg.getBytes("UTF-8");

                URLConnection con = url.openConnection();
                https = (HttpsURLConnection) con;
                https.setRequestMethod("POST");
                https.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                https.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                https.setDoOutput(true);
                out = https.getOutputStream();
                out.write(postDataBytes);

                in = new InputStreamReader(https.getInputStream(), "UTF-8");
                reader = new BufferedReader(in);
                System.out.println(reader.readLine() + "!!!!");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    https.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
