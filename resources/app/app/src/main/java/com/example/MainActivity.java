package com.example;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity{

    private static final String pin = "2222";
    private String username;
    LinearLayout layout;
    private static final String CHANNEL_ID = "intruder";

    final Handler handler = new Handler(Looper.getMainLooper());
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_secreen);
        createNotificationChannel();
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

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void setValid(String bool,String id){
        EditText password1 = (EditText) findViewById(R.id.password);
        TextView invalid = (TextView) findViewById(R.id.invalid);
        username = id;
        if(bool.equals("True")){
            GetHouse gh = new GetHouse();
            gh.run(((MyApplication) this.getApplication()),id);
            setContentView(R.layout.pin_page);
            pinFunctionality();
        }
        else{
            password1.setText("");
            invalid.setText("Invalid Username Or Password.");
        }
    }


    public void loginFunctionality(){
        Button submit = (Button) findViewById(R.id.loginbutton);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText email1 = (EditText) findViewById(R.id.username);
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

    private void displayHouses(){
        ArrayList<String> house = (((MyApplication) this.getApplication()).getHouseNumbers());
        if(house.size()>1){
            int index = 0;
            setContentView(R.layout.house_page);
            TextView welcome = findViewById(R.id.welcome_message);
            layout = findViewById(R.id.linearLayout);
            welcome.setText("Welcome " +username +"! Chose which house you would like to control. ");
            for(String x: house){
                final Button button = new Button(this);
                button.setLayoutParams(new LinearLayout.LayoutParams(-1,150));
                button.setId(index);
                button.setText(x);
                layout.addView(button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //set current house id
                        finish();
                        Intent i = new Intent(MainActivity.this, FunctionMenu.class);
                        i.putExtra("HOUSE_SESSION_ID", button.getText().toString());
                        startActivity(i);
                    }
                });
                index += 1;
            }

        }
        else if(house.size() == 1){
            finish();
            Intent i = new Intent(MainActivity.this, FunctionMenu.class);
            i.putExtra("HOUSE_SESSION_ID",house.get(0));
            startActivity(i);
        }
    }


    public void pinFunctionality() {
        final EditText pinField = (EditText) findViewById(R.id.digit1);
        pinField.requestFocus();
        pinField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 4){
                    if(pinField.getText().toString().equals(pin)){
                        displayHouses();
                    }
                    else{
                        pinField.setText("");
                        TextView textView1 = (TextView) findViewById(R.id.textView1);
                        textView1.setText("Incorrect pin, please try again");
                    }
                }
            }
        });
    }

    public class CheckLogin extends AsyncTask {
        private static final String raptor="https://www.cs.kent.ac.uk/people/staff/ds710/co600/";
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
            setValid(result.toString(),id);

        }

    }
}


