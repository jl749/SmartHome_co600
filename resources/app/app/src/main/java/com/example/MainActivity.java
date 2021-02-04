package com.example;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity{

    private static final String pin = "2222";

    final Handler handler = new Handler(Looper.getMainLooper());
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome_secreen);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.login_page);
                //setContentView(R.layout.pin_page);
                loginFunctionality();
                //pinFunctionality();
            }
        }, 3000);
    }

    public void setValid(String bool){
        final EditText password1 = (EditText) findViewById(R.id.password);
        if(bool.equals("True")){
            setContentView(R.layout.pin_page);
            pinFunctionality();
        }
        else{
            password1.setText("");
            System.out.println("FAIL");
        }
    }


    public void loginFunctionality(){
        Button submit = (Button) findViewById(R.id.loginbutton);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText email1 = (EditText) findViewById(R.id.email);
                CharSequence email = email1.getText();
                EditText password1 = (EditText) findViewById(R.id.password);
                CharSequence password = password1.getText();
                if (!TextUtils.isEmpty(email)){
                    if(!TextUtils.isEmpty(password)) {
                        String ePassword = (Encryption.encryptPassword(password.toString())).toUpperCase();
                        CheckLogin task = new CheckLogin(email.toString(),ePassword);
                        task.execute();
                    }
                    else password1.setError("Enter a password");
                }
                else email1.setError("Enter a username");
            }
        });
    }


    public void pinFunctionality() {
        final EditText text1 = (EditText) findViewById(R.id.digit1);
        text1.requestFocus();
        text1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 4){
                    if(text1.getText().toString().equals(pin)){
                        finish();
                        Intent i = new Intent(MainActivity.this, FunctionMenu.class);
                        startActivity(i);
                    }
                    else{
                        text1.setText("");
                        TextView textView1 = (TextView) findViewById(R.id.textView1);
                        textView1.setText("Incorrect pin, please try again");
                    }
                }
            }
        });
    }

    public class CheckLogin extends AsyncTask {
        private static final String raptor="https://raptor.kent.ac.uk/~jl749/";
        StringBuilder result=new StringBuilder();
        HttpsURLConnection https=null;
        OutputStream out=null;
        InputStreamReader in=null;
        BufferedReader reader=null;
        String id;
        String pass;

        public CheckLogin(String username, String password){
            super();
            id = username;
            pass = password;
        }

        @Override
        protected Void doInBackground(Object... objects) {

            try {
                URL url = new URL(raptor+"chkLogin.php");
                String msg="id="+id+"&pass="+pass;
                byte[] postDataBytes=msg.getBytes("UTF-8");

                URLConnection con = url.openConnection();
                https = (HttpsURLConnection)con;
                https.setRequestMethod("POST");
                https.setConnectTimeout(3000);
                https.setReadTimeout(2000);
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
                try{reader.close();}catch(Exception e) {e.printStackTrace();}
                try{in.close();}catch(Exception e) {e.printStackTrace();}
                try{out.close();}catch(Exception e) {e.printStackTrace();}
                try {https.disconnect();}catch(Exception e) {e.printStackTrace();}
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            setValid(result.toString());

        }

    }
}


