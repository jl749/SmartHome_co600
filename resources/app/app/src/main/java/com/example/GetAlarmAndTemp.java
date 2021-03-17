package com.example;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GetAlarmAndTemp extends Activity {

    public void  run(MyApplication m,String houseID){
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
            System.out.println(array);
            String lock = array.get(0).replace("\"", "");
            String alarm = array.get(1).replace("\"", "");
            String ttemp = array.get(2).replace("\"", "");
            m.setTargetTemp(ttemp);
            if(alarm.equals("1")) {
                m.setAlarm("1");
            }
            else if(alarm.equals(("0"))) {
                m.setAlarm("0");}
        }
    }

    private  class GetAandT extends AsyncTask<Void, Void, Void> {

        //private static final String raptor = "https://raptor.kent.ac.uk/~jl749/";
        private String raptor = MainActivity.raptor;
        Map<String, String> values = new HashMap<>();
        HttpURLConnection http = null;
        OutputStream out = null;
        InputStreamReader in = null;
        BufferedReader reader = null;
        String houseID;
        MyApplication m;

        public GetAandT(String houseID,MyApplication m) {
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
                http = (HttpURLConnection) con;
                http.setRequestMethod("POST");
                http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                http.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                http.setDoOutput(true);
                out = http.getOutputStream();
                out.write(postDataBytes);

                String line;
                in = new InputStreamReader(http.getInputStream(), "UTF-8");
                reader = new BufferedReader(in);
                while ((line = reader.readLine()) != null) {
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
                    http.disconnect();
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
