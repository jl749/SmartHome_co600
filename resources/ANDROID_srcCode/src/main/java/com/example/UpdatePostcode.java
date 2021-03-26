package com.example;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class UpdatePostcode {

    public void run(String houseID, String postcode){
        UpdatePC upc = new UpdatePC(houseID, postcode);
        upc.execute();
    }

    public static class UpdatePC extends AsyncTask<Void, Void, Void> {

        String houseID;
        String postCode;
        private static final String raptor = MainActivity.raptor;

        public UpdatePC(String houseID, String postCode) {
            super();
            this.postCode = postCode;
            this.houseID = houseID;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpURLConnection http=null;
            OutputStream out=null;
            InputStreamReader in=null;
            BufferedReader reader=null;
            try {
                URL url = new URL(raptor+"updatePOSTCODE.php");
                String msg="houseID="+houseID+"&PostCode="+postCode;
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
                System.out.println(reader.readLine());
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
    }
}
