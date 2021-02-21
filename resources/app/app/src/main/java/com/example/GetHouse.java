package com.example;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class GetHouse extends Activity {

    public void run(MyApplication m,String username){
        GetH houses = new GetH(m,username);
        houses.execute();
    }

    public void setHouses(MyApplication m,List<String> housenumbers){
        ArrayList<String> hn = new ArrayList<>(housenumbers);
        m.setHouseNumbers(hn);

    }

    private class GetH extends AsyncTask<Void, Void, Void>{

        private static final String raptor = "https://www.cs.kent.ac.uk/people/staff/ds710/co600/";
        List<String> numbers =new ArrayList<>();
        HttpsURLConnection https=null;
        OutputStream out=null;
        InputStreamReader in=null;
        BufferedReader reader=null;
        String username;
        MyApplication m;


        public GetH(MyApplication m,String username){
            super();
            this.username = username;
            this.m = m;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(raptor+"getHouseRegistered.php");
                String msg="username="+username;
                byte[] postDataBytes=msg.getBytes("UTF-8");

                URLConnection con = url.openConnection();
                https = (HttpsURLConnection)con;
                https.setRequestMethod("POST");
                https.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                https.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                https.setDoOutput(true);
                out=https.getOutputStream();
                out.write(postDataBytes);

                String line;
                in=new InputStreamReader(https.getInputStream(),"UTF-8");
                reader=new BufferedReader(in);
                while((line=reader.readLine())!=null) {
                    System.out.println(line+"!!");
                    numbers.add(line);
                }
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
            System.out.println(numbers);
            setHouses(m,numbers);
        }

    }
}
