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
import java.util.List;


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

        private static final String raptor = MainActivity.raptor;
        List<String> numbers =new ArrayList<>();
        HttpURLConnection http=null;
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
                http = (HttpURLConnection)con;
                http.setRequestMethod("POST");
                http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                http.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                http.setDoOutput(true);
                out=http.getOutputStream();
                out.write(postDataBytes);

                String line;
                in=new InputStreamReader(http.getInputStream(),"UTF-8");
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
                try{http.disconnect();}catch(Exception e) {e.printStackTrace();}
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
