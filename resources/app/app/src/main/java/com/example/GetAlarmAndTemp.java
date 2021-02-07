package com.example;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class GetAlarmAndTemp extends Activity {

    public void  run(MyApplication m,int houseID){
        GetAandT alramandtemp = new GetAandT(houseID,m);
        alramandtemp.execute();
    }

    public void setVariables(ArrayList<String> array, MyApplication m) {
        boolean connected = true;
        if(array.isEmpty()){
            connected = false;
            m.setConnectionT();
            System.out.println("Could not establish connection");
        }
        if(connected) {
            m.setConnectionF();
            String alarm = array.get(0).replace("\"", "");
            String ttemp = array.get(1).replace("\"", "");
            m.setTargetTemp(ttemp);
            if(alarm.equals("1")) {
                m.setAlarm("True");
            }
            else{m.setAlarm("False");}
        }
    }

    private class GetAandT extends AsyncTask<Void, Void, Void> {

        private static final String raptor = "https://raptor.kent.ac.uk/~jl749/";
        Map<String, String> values = new HashMap<>();
        HttpsURLConnection https = null;
        OutputStream out = null;
        InputStreamReader in = null;
        BufferedReader reader = null;
        int houseID;
        MyApplication m;

        public GetAandT(int houseID,MyApplication m) {
            super();
            this.houseID = houseID;
            this.m = m;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(raptor + "getThreshold.php");
                String msg = "houseID=" + houseID;
                byte[] postDataBytes = msg.getBytes("UTF-8");

                URLConnection con = url.openConnection();
                https = (HttpsURLConnection) con;
                https.setRequestMethod("POST");
                https.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                https.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                https.setDoOutput(true);
                out = https.getOutputStream();
                out.write(postDataBytes);

                String line;
                in = new InputStreamReader(https.getInputStream(), "UTF-8");
                reader = new BufferedReader(in);
                while ((line = reader.readLine()) != null) {
                    System.out.println(line + "!!");
                    String[] tmp = line.split("=");
                    values.put(tmp[0], tmp[1]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        @Override
        protected void onPostExecute(Void result){
            Collection<String> temp = values.values();
            ArrayList<String> listOfValues = new ArrayList<String>(temp);
            setVariables(listOfValues,m);
        }
    }
}
