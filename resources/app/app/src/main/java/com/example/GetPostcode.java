package com.example;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class GetPostcode {

    public void run(String houseID,MyApplication m){
        GetPC pc = new GetPC(houseID,m);
        pc.execute();
    }

    public void setPostcode(String postcode,MyApplication m){
        if(postcode != null){
            m.setPostCode(postcode);
        }
    }

    public class GetPC extends AsyncTask<Void, Void, Void> {

        String houseID;
        MyApplication m;
        String postCode;

        private static final String raptor = MainActivity.raptor;

        public GetPC(String houseID,MyApplication m) {
            super();
            this.houseID = houseID;
            this.m = m;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            HttpURLConnection http=null;
            OutputStream out=null;
            InputStreamReader in=null;
            BufferedReader reader=null;
            try {
                URL url = new URL(raptor+"getPOSTCODE.php");
                String msg="houseID="+houseID;
                byte[] postDataBytes=msg.getBytes("UTF-8");

                URLConnection con = url.openConnection();
                http = (HttpURLConnection)con;
                http.setRequestMethod("POST");
                http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                http.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                http.setDoOutput(true);
                http.setDoInput(true);
                out=http.getOutputStream();
                out.write(postDataBytes);

                in=new InputStreamReader(http.getInputStream(),"UTF-8");
                reader=new BufferedReader(in);
                postCode=reader.readLine();
            }catch(Exception e) {
                e.printStackTrace();
            }finally {
                try{reader.close();}catch(Exception e) {e.printStackTrace();}
                try{in.close();}catch(Exception e) {e.printStackTrace();}
                try{out.close();}catch(Exception e) {e.printStackTrace();}
                try{http.disconnect();}catch(Exception e) {e.printStackTrace();}
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            setPostcode(postCode,m);
        }
    }
}

