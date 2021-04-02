package com.example;

import androidx.appcompat.app.AppCompatActivity;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


/**Main activity class
 * This activity is called when the app is started
 */

public class MainActivity extends AppCompatActivity{

    static final String nodMCUwebServer="http://192.168.1.9/";
    static final String raptor ="http://192.168.1.8/co600/";
    //static final String raptor ="https://raptor.kent.ac.uk/~jl749/";
    private String pin = "2222";
    private String username;
    LinearLayout layout;
    private static final String CHANNEL_ID = "intruder";
    final Handler handler = new Handler(Looper.getMainLooper());

    /*Shows welcome screen and checks if log in binary files exists.
    * If binary file exists it will attempt to log in with the credentials stores
    * and redirect you to pin page, else it displays the log in page */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_secreen);
        DataMining.setPath(getApplicationContext().getFilesDir().toString()+"/");
        ((MyApplication)this.getApplication()).setFirstOpen(true);
        ((MyApplication)this.getApplication()).setDataminingStatus(false);
        createNotificationChannel();
        File f = new File(getApplicationContext().getFilesDir() +"/BINARY_DIR.DAT");
        File p = new File(getApplicationContext().getFilesDir()+"/BINARY_PIN.DAT");
        checkCustomPin(p);
        if(f.exists()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String username = BinaryFile.readBinaryOBJ(getApplicationContext()).getID();
                    String password =  BinaryFile.readBinaryOBJ(getApplicationContext()).getPass();
                    CheckLogin task = new CheckLogin(username,password,true);
                    task.execute();
                }
            }, 3000);
        }
        else{
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setContentView(R.layout.login_page);
                    loginFunctionality();
                }
            }, 3000);
        }
    }

    /*Create notification channel for intrusion alert notification*/
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /*Redirects to pin page upon valid log in attempt*/
    public void setValid(String bool,String id,String password,boolean fileExists){
        EditText password1 = (EditText) findViewById(R.id.password);
        TextView invalid = (TextView) findViewById(R.id.invalid);
        username = id;
        if(bool.equals("True")){
            GetHouse gh = new GetHouse();
            gh.run(((MyApplication) this.getApplication()),id);
            setContentView(R.layout.pin_page);
            pinFunctionality();
            BinaryFile.writeBinaryOBJ(username,password,getApplicationContext());
        }
        else{
            if(fileExists){
                setContentView(R.layout.login_page);
                loginFunctionality();
            }
            else{
                password1.setText("");
                invalid.setText("Invalid Username Or Password.");
            }
        }
    }

    /*Displays log in page*/
    public void loginFunctionality(){
        Button submit = (Button) findViewById(R.id.loginbutton);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText email1 = (EditText) findViewById(R.id.username);
                String emailRaw = email1.getText().toString();
                String email = emailRaw.replaceAll("[^a-zA-Z0-9]", "");
                EditText password1 = (EditText) findViewById(R.id.password);
                CharSequence password = password1.getText();
                if (!TextUtils.isEmpty(email)){
                    if(!TextUtils.isEmpty(password)) {
                        String ePassword = (Encryption.encryptPassword(password.toString())).toUpperCase();
                        CheckLogin cl = new CheckLogin(email,ePassword,false);
                        cl.execute();
                    }
                    else password1.setError("Enter a password");
                }
                else email1.setError("Enter a username");
            }
        });
    }

    /*If user has more than 1 house, page to choose house dynamically created.
    *If user only has 1 house it redirects straight to menu.
    * Also creates .arff file used for dataming if not created yet
    */
    private void displayHouses(){
        if(!DataMining.getFileExists(username)){
            DataMining.GET().initARFF_file(username);
        }
        ArrayList<String> house = (((MyApplication) this.getApplication()).getHouseNumbers());
        ((MyApplication)this.getApplication()).setUsername(username);
        if(house.size()>1){
            int index = 0;
            setContentView(R.layout.house_page);
            TextView welcome = findViewById(R.id.welcome_message);
            layout = findViewById(R.id.linearLayout);
            String welcomeText = ("Welcome " +username +"! Choose which house you would like to control. ");
            welcome.setText(welcomeText);
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

    /*Displays pin page*/
    public void pinFunctionality() {
        final EditText pinField = (EditText) findViewById(R.id.digit1);
        pinField.requestFocus();
        pinField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 4){
                    if(pinField.getText().toString().equals(pin)){
                        displayHouses();
                        setCurrentPin(pinField.getText().toString());
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

    /*Current pin global variable setter and writes current pin to binary file*/
    private void setCurrentPin(String pin){
        ((MyApplication)this.getApplication()).setCurrentPin(pin);
        PinFile.writeBinaryOBJ(pin,getApplicationContext());
    }

    /*Checks if pin has been changed and is stored in binary file*/
    private void checkCustomPin(File p){
        if(p.exists()){
            pin = PinFile.readBinaryOBJ(getApplicationContext()).getPin();
        }
    }

    /*Checks login credentials with database values*/
    private class CheckLogin extends AsyncTask<Void, Void, Void> {

        private static final String raptor=MainActivity.raptor;
        StringBuilder result=new StringBuilder();
        HttpURLConnection http=null;
        OutputStream out=null;
        InputStreamReader in=null;
        BufferedReader reader=null;
        String id;
        String pass;
        Boolean fileExists;

        public CheckLogin(String username, String password, boolean fileExists){
            super();
            id = username;
            pass = password;
            this.fileExists = fileExists;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                URL url = new URL(raptor+"chkLogin.php");
                String msg="id="+id+"&pass="+pass;
                byte[] postDataBytes=msg.getBytes("UTF-8");

                URLConnection con = url.openConnection();
                http = (HttpURLConnection)con;
                http.setRequestMethod("POST");
                http.setConnectTimeout(3000);
                http.setReadTimeout(2000);
                http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                http.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                http.setDoOutput(true);
                out=http.getOutputStream();
                out.write(postDataBytes);

                in=new InputStreamReader(http.getInputStream(),"UTF-8");
                reader=new BufferedReader(in);
                result.append(reader.readLine());
            }catch(Exception e) {
                e.printStackTrace();
            }finally {
                try{reader.close();}catch(Exception e) {e.printStackTrace();}
                try{in.close();}catch(Exception e) {e.printStackTrace();}
                try{out.close();}catch(Exception e) {e.printStackTrace();}
                try {http.disconnect();}catch(Exception e) {e.printStackTrace();}
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void results) {
            setValid(result.toString(),id,pass,fileExists);
        }
    }
}


