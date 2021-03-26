package com.example;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


public class SetAlarm extends Activity {

    public void run(boolean b,String houseID){
        UpdateIntruder update = new UpdateIntruder(b, houseID);
        update.execute();
    }

    private static class UpdateIntruder extends AsyncTask<Void,Void,Void>{

        HttpURLConnection http=null;
        OutputStream out=null;
        InputStreamReader in=null;
        BufferedReader reader=null;
        boolean b;
        String houseID;

        private static final String raptor = MainActivity.raptor;

        public UpdateIntruder(boolean b,String houseID){
            super();
            this.b = b;
            this.houseID = houseID;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(raptor+"updateThreshold.php");
                int val=(b) ? 1 : 0;
                String msg="intruder="+val+"&houseID="+houseID;
                byte[] postDataBytes=msg.getBytes("UTF-8");

                URLConnection con = url.openConnection();
                http = (HttpURLConnection)con;
                http.setRequestMethod("POST");
                http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                http.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                http.setDoOutput(true);
                out=http.getOutputStream();
                out.write(postDataBytes);

                in=new InputStreamReader(http.getInputStream(),"UTF-8");
                reader=new BufferedReader(in);
                System.out.println(reader.readLine()+"!!!!");
            }catch(Exception e) {
                e.printStackTrace();
            }finally {
                try{out.close();}catch(Exception e) {e.printStackTrace();}
                try {http.disconnect();}catch(Exception e) {e.printStackTrace();}
            }
            return null;
        }
    }
}
