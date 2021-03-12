package com.example;
//import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;


public class CheckLogin {
    private static final String raptor="https://www.cs.kent.ac.uk/people/staff/ds710/co600/";

    public  boolean chkLogin(String id, String pass) {
        StringBuilder result=new StringBuilder();

        OutputStream out=null;
        InputStreamReader in=null;
        BufferedReader reader=null;
        try {
            URL url = new URL(raptor+"chkLogin.php");
            String msg="id="+id+"&pass="+pass;
            byte[] postDataBytes=msg.getBytes("UTF-8");

            URLConnection con = url.openConnection();
            HttpsURLConnection https = (HttpsURLConnection)con;
            https.setRequestMethod("POST");
            https.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            https.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            https.setDoOutput(true);
            out=https.getOutputStream();
            out.write(postDataBytes);

            in=new InputStreamReader(https.getInputStream(),"UTF-8");
            reader=new BufferedReader(in);
            result.append(reader.readLine());
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            //con.close();
            try{reader.close();}catch(Exception e) {e.printStackTrace();}
            try{in.close();}catch(Exception e) {e.printStackTrace();}
            try{out.close();}catch(Exception e) {e.printStackTrace();}
        }
        System.out.println(result.toString()+"!");
        if(result.toString().equals("True"))
            return true;
        else
            return false;
    }

    public void updateTMP(double val) {

    }

    public  void main(String[] args) {
        chkLogin("John98","19513FDC9DA4FB72A4A05EB66917548D3C90FF94D5419E1F2363EEA89DFEE1DD");
    }
}