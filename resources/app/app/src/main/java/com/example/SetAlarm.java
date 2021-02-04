package com.example;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

public class SetAlarm extends Activity {

    public void run(boolean b,int houseID){
        UpdateIntruder update = new UpdateIntruder(b,houseID);
        update.execute();
    }

    private class UpdateIntruder extends AsyncTask<Void,Void,Void>{

        HttpsURLConnection https=null;
        OutputStream out=null;
        InputStreamReader in=null;
        BufferedReader reader=null;
        boolean b;
        int houseID;
        private static final String raptor = "https://raptor.kent.ac.uk/~jl749/";

        public UpdateIntruder(boolean b,int houseID){
            super();
            this.b = b;
            this.houseID = houseID;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(raptor+"update_tmp_intruder.php");
                int val=(b) ? 1 : 0;
                String msg="intruder="+val+"&houseID="+houseID;
                byte[] postDataBytes=msg.getBytes("UTF-8");

                URLConnection con = url.openConnection();
                https = (HttpsURLConnection)con;
                https.setRequestMethod("POST");
                https.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                https.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                https.setDoOutput(true);
                out=https.getOutputStream();
                out.write(postDataBytes);

                in=new InputStreamReader(https.getInputStream(),"UTF-8");
                reader=new BufferedReader(in);
                System.out.println(reader.readLine()+"!!!!");
            }catch(Exception e) {
                e.printStackTrace();
            }finally {
                try{out.close();}catch(Exception e) {e.printStackTrace();}
                try {https.disconnect();}catch(Exception e) {e.printStackTrace();}
            }
            return null;
        }
    }
}
