package com.example;

import android.app.Activity;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;


import javax.net.ssl.HttpsURLConnection;

public class GetAPIKey extends Activity {

    public void run(MyApplication m,String houseID){
        GetAPIK a = new GetAPIK(m,houseID);
        a.execute();
    }

    public void setAPIkey(String apiKey,MyApplication m){
        m.setAPIKey(apiKey);
    }


    private  class GetAPIK extends AsyncTask<Void, Void, Void> {

        private static final String raptor = "https://raptor.kent.ac.uk/~jl749/";
        HttpsURLConnection https = null;
        OutputStream out = null;
        InputStreamReader in = null;
        BufferedReader reader = null;
        String apiKey=null;
        String houseID;
        MyApplication m;

        public GetAPIK(MyApplication m,String houseID) {
            super();
            this.houseID = houseID;
            this.m = m;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(raptor+"getAPIKey.php");
                String msg="houseID="+houseID;
                byte[] postDataBytes=msg.getBytes("UTF-8");

                URLConnection con = url.openConnection();
                https = (HttpsURLConnection)con;
                https.setRequestMethod("POST");
                https.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                https.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                https.setConnectTimeout(1000);
                https.setReadTimeout(1000);
                https.setDoOutput(true);
                https.setDoInput(true);
                out=https.getOutputStream();
                out.write(postDataBytes);

                in=new InputStreamReader(https.getInputStream(),"UTF-8");
                reader=new BufferedReader(in);
                apiKey=reader.readLine();
            }catch(Exception e) {
                e.printStackTrace();
            }finally {
                try{reader.close();}catch(Exception e) {e.printStackTrace();}
                try{in.close();}catch(Exception e) {e.printStackTrace();}
                try{out.close();}catch(Exception e) {e.printStackTrace();}
                try{https.disconnect();}catch(Exception e) {e.printStackTrace();}
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            setAPIkey(apiKey,m);
        }
    }

}
